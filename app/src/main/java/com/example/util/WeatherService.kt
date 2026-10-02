package com.example.util

import android.content.Context
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/**
 * A single real weather observation for the home screen clock header.
 */
data class WeatherSnapshot(
    val temperatureC: Int,
    val condition: String,
    val city: String?,
    val fetchedAt: Long = System.currentTimeMillis()
)

/**
 * Fetches real local weather without requiring any API key or location permission:
 *  1. Approximate coordinates + city from the public IP (https://ipwho.is).
 *  2. Current conditions from Open-Meteo (https://open-meteo.com).
 *
 * Results are cached in SharedPreferences for 30 minutes so the header renders
 * instantly on launch and still works offline with the last known reading.
 */
object WeatherService {

    private const val PREFS = "atlas_weather_cache"
    private const val KEY_SNAPSHOT = "snapshot_json"
    private const val KEY_FETCHED_AT = "fetched_at"
    private const val CACHE_TTL_MS = 30 * 60 * 1000L

    private val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    /** Returns the cached reading if present, regardless of age. */
    fun loadCached(context: Context): WeatherSnapshot? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_SNAPSHOT, null) ?: return null
        return parse(raw)
    }

    /** True when the cache is fresh enough to skip a network round trip. */
    fun isFresh(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val fetchedAt = prefs.getLong(KEY_FETCHED_AT, 0L)
        return System.currentTimeMillis() - fetchedAt < CACHE_TTL_MS
    }

    /** Network fetch with graceful fallback to the cached reading. */
    suspend fun fetch(context: Context): WeatherSnapshot? = withContext(Dispatchers.IO) {
        try {
            val cached = loadCached(context)
            if (cached != null && isFresh(context)) return@withContext cached

            val coordinates = fetchCoordinates()
            val reading = fetchCurrentWeather(coordinates.first, coordinates.second)
            if (reading != null) {
                val snapshot = WeatherSnapshot(
                    temperatureC = reading.first,
                    condition = reading.second,
                    city = coordinates.third
                )
                save(context, snapshot)
                snapshot
            } else {
                cached
            }
        } catch (e: Exception) {
            loadCached(context)
        }
    }

    private fun save(context: Context, snapshot: WeatherSnapshot) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_SNAPSHOT, toJson(snapshot))
            .putLong(KEY_FETCHED_AT, System.currentTimeMillis())
            .apply()
    }

    /** Latitude, longitude and city for the current public IP. */
    private fun fetchCoordinates(): Triple<Double, Double, String?> {
        val request = Request.Builder().url("https://ipwho.is/").get().build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return Triple(0.0, 0.0, null)
            val json = JSONObject(response.body?.string().orEmpty())
            if (!json.optBoolean("success", false)) return Triple(0.0, 0.0, null)
            val lat = json.optDouble("latitude", 0.0)
            val lon = json.optDouble("longitude", 0.0)
            val city = json.optString("city").orEmpty().ifBlank { null }
            return Triple(lat, lon, city)
        }
    }

    /** Current temperature and human readable condition for a coordinate pair. */
    private fun fetchCurrentWeather(lat: Double, lon: Double): Pair<Int, String>? {
        val url = "https://api.open-meteo.com/v1/forecast" +
            "?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code&timezone=auto"
        val request = Request.Builder().url(url).get().build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val json = JSONObject(response.body?.string().orEmpty())
            val current = json.optJSONObject("current") ?: return null
            val temperature = current.optDouble("temperature_2m", Double.NaN)
            if (temperature.isNaN()) return null
            val code = current.optInt("weather_code", -1)
            return Pair(temperature.toInt(), conditionFor(code))
        }
    }

    /** WMO weather interpretation codes -> short labels shown in the clock pill. */
    private fun conditionFor(code: Int): String = when (code) {
        0 -> "Clear"
        1 -> "Mostly Clear"
        2 -> "Partly Cloudy"
        3 -> "Overcast"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing Drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing Rain"
        71, 73, 75, 77 -> "Snow"
        80, 81, 82 -> "Showers"
        85, 86 -> "Snow Showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm"
        else -> "Cloudy"
    }

    private fun toJson(snapshot: WeatherSnapshot): String = JSONObject().apply {
        put("t", snapshot.temperatureC)
        put("c", snapshot.condition)
        put("city", snapshot.city ?: "")
        put("at", snapshot.fetchedAt)
    }.toString()

    private fun parse(raw: String): WeatherSnapshot? = try {
        val json = JSONObject(raw)
        WeatherSnapshot(
            temperatureC = json.optInt("t"),
            condition = json.optString("c", "Clear"),
            city = json.optString("city").orEmpty().ifBlank { null },
            fetchedAt = json.optLong("at", 0L)
        ).takeIf { it.condition.isNotBlank() }
    } catch (e: Exception) {
        null
    }
}
