package com.example.util

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * Thrown when the Gemini API is unreachable, unconfigured or returns an error payload.
 */
class GeminiException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Thin client for the Google Generative Language (Gemini) REST API.
 *
 * The API key is produced by the secrets Gradle plugin from the `.env` file
 * (`GEMINI_API_KEY=...`) as `BuildConfig.GEMINI_API_KEY`. The key is resolved
 * reflectively so the project still compiles on machines where that property is
 * absent (AI Studio injects it at build time), and a clear error is surfaced at
 * runtime instead of a compile failure.
 */
object GeminiClient {

    private const val MODEL = "gemini-2.5-flash"
    private const val ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    private val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    /** Resolved once: `BuildConfig.GEMINI_API_KEY` if the secrets plugin generated it. */
    val apiKey: String? by lazy {
        try {
            val buildConfig = Class.forName("com.example.BuildConfig")
            val field = buildConfig.getDeclaredField("GEMINI_API_KEY")
            field.isAccessible = true
            (field.get(null) as? String)?.trim()?.takeIf { it.isNotEmpty() && !it.startsWith("MY_") }
        } catch (e: Throwable) {
            null
        }
    }

    val isConfigured: Boolean
        get() = !apiKey.isNullOrBlank()

    /**
     * Single-turn text generation with a system instruction.
     * Runs on [Dispatchers.IO] and throws [GeminiException] on any failure.
     */
    suspend fun generate(systemInstruction: String, prompt: String): String =
        withContext(Dispatchers.IO) {
            val key = apiKey ?: throw GeminiException(
                "No Gemini API key found. Add GEMINI_API_KEY=... to the .env file in the project root."
            )

            val body = JSONObject().apply {
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 600)
                })
            }

            val request = Request.Builder()
                .url("$ENDPOINT?key=$key")
                .post(body.toString().toRequestBody(mediaType))
                .build()

            val response = try {
                http.newCall(request).execute()
            } catch (e: Exception) {
                throw GeminiException("Couldn't reach Gemini. Check your internet connection.", e)
            }

            response.use {
                val payload = it.body?.string().orEmpty()
                if (!it.isSuccessful) {
                    throw GeminiException(humaniseError(it.code, payload))
                }
                extractText(payload).ifBlank {
                    throw GeminiException("Gemini returned an empty response. Try rephrasing.")
                }
            }
        }

    /**
     * Asks Gemini for 3 concrete micro-steps for a task, returned as plain lines.
     */
    suspend fun suggestMicroSteps(
        taskTitle: String,
        taskDescription: String,
        existingSteps: List<String> = emptyList()
    ): List<String> {
        val system = """
            You are Atlas, an execution-first focus coach inside an Android launcher.
            Break a task into the smallest possible next actions.
            Reply with EXACTLY 3 lines and nothing else: no numbering, no bullets, no preamble.
            Each line must be a concrete action of at most 12 words that can be started immediately.
        """.trimIndent()

        val existing = if (existingSteps.isEmpty()) "none yet" else existingSteps.joinToString(" | ")
        val prompt = """
            Task: $taskTitle
            Details: ${taskDescription.ifBlank { "none" }}
            Existing micro-steps: $existing
        """.trimIndent()

        val raw = generate(system, prompt)
        return raw.lines()
            .map { it.trim().removePrefix("- ").removePrefix("* ").trim() }
            .filter { it.isNotBlank() }
            .take(3)
    }

    private fun extractText(payload: String): String {
        return try {
            val root = JSONObject(payload)
            val candidates = root.optJSONArray("candidates") ?: return ""
            if (candidates.length() == 0) return ""
            val content = candidates.getJSONObject(0).optJSONObject("content") ?: return ""
            val parts = content.optJSONArray("parts") ?: return ""
            val builder = StringBuilder()
            for (i in 0 until parts.length()) {
                val text = parts.getJSONObject(i).optString("text")
                if (text.isNotBlank()) builder.append(text)
            }
            builder.toString().trim()
        } catch (e: Exception) {
            ""
        }
    }

    private fun humaniseError(code: Int, payload: String): String {
        val detail = try {
            JSONObject(payload)
                .optJSONObject("error")
                ?.optString("message")
                .orEmpty()
        } catch (e: Exception) {
            ""
        }
        return when {
            code == 400 || code == 403 || detail.contains("API key", ignoreCase = true) ->
                "Gemini rejected the API key. Check GEMINI_API_KEY in your .env file."
            code == 429 ->
                "Gemini rate limit reached. Try again in a moment."
            code in 500..599 ->
                "Gemini is having trouble right now. Try again shortly."
            detail.isNotBlank() -> detail
            else -> "Gemini request failed (HTTP $code)."
        }
    }
}
