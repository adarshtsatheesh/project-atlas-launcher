package com.example.avatar

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import org.json.JSONObject

/**
 * Robust local persistence manager for the user's digital avatar.
 * Guarantees avatar is stored locally and loaded with 0 latency on launch.
 */
class AvatarStorage(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun hasSavedAvatar(): Boolean {
        return prefs.contains(KEY_AVATAR_JSON) || prefs.contains(KEY_IS_GENERATED)
    }

    fun saveAvatar(config: AvatarConfig) {
        val json = JSONObject().apply {
            put("skinTone", config.skinTone.name)
            put("faceShape", config.faceShape.name)
            put("hairStyle", config.hairStyle.name)
            put("hairColor", config.hairColor.name)
            put("eyeShape", config.eyeShape.name)
            put("eyeColor", config.eyeColor.name)
            put("eyebrowStyle", config.eyebrowStyle.name)
            put("beardStyle", config.beardStyle.name)
            put("beardColor", config.beardColor.name)
            put("outfitType", config.outfitType.name)
            put("outfitPrimaryColor", config.outfitPrimaryColor)
            put("outfitSecondaryColor", config.outfitSecondaryColor)
            put("pantsType", config.pantsType.name)
            put("pantsColor", config.pantsColor)
            put("footwear", config.footwear.name)
            put("footwearColor", config.footwearColor)
            put("glasses", config.glasses.name)
            put("watch", config.watch.name)
            put("hat", config.hat.name)
            put("genderPresentation", config.genderPresentation.name)
            put("version", config.version)
        }

        prefs.edit()
            .putString(KEY_AVATAR_JSON, json.toString())
            .putBoolean(KEY_IS_GENERATED, true)
            .apply()

        AvatarCache.update(config)
    }

    fun loadAvatar(): AvatarConfig {
        AvatarCache.get()?.let { return it }

        val raw = prefs.getString(KEY_AVATAR_JSON, null)
        if (raw == null) {
            val fallback = AvatarConfig.DEFAULT_MASCULINE
            AvatarCache.update(fallback)
            return fallback
        }

        return try {
            val json = JSONObject(raw)
            val config = AvatarConfig(
                skinTone = safeValueOf(json.optString("skinTone"), SkinTone.WARM_IVORY),
                faceShape = safeValueOf(json.optString("faceShape"), FaceShape.OVAL_BALANCED),
                hairStyle = safeValueOf(json.optString("hairStyle"), HairStyle.SHORT_FADE),
                hairColor = safeValueOf(json.optString("hairColor"), HairColor.JET_BLACK),
                eyeShape = safeValueOf(json.optString("eyeShape"), EyeShape.ALMOND_FOCUSED),
                eyeColor = safeValueOf(json.optString("eyeColor"), EyeColor.DARK_BROWN),
                eyebrowStyle = safeValueOf(json.optString("eyebrowStyle"), EyebrowStyle.CLEAN_NATURAL),
                beardStyle = safeValueOf(json.optString("beardStyle"), BeardStyle.CLEAN_SHAVEN),
                beardColor = safeValueOf(json.optString("beardColor"), HairColor.JET_BLACK),
                outfitType = safeValueOf(json.optString("outfitType"), OutfitType.MINIMAL_HOODIE),
                outfitPrimaryColor = json.optLong("outfitPrimaryColor", 0xFF141A29),
                outfitSecondaryColor = json.optLong("outfitSecondaryColor", 0xFF00C8FF),
                pantsType = safeValueOf(json.optString("pantsType"), PantsType.TAILORED_JOGGERS),
                pantsColor = json.optLong("pantsColor", 0xFF0F131D),
                footwear = safeValueOf(json.optString("footwear"), FootwearType.MINIMAL_SNEAKERS),
                footwearColor = json.optLong("footwearColor", 0xFFE2E8F0),
                glasses = safeValueOf(json.optString("glasses"), GlassesType.NONE),
                watch = safeValueOf(json.optString("watch"), WatchType.NONE),
                hat = safeValueOf(json.optString("hat"), HatType.NONE),
                genderPresentation = safeValueOf(json.optString("genderPresentation"), GenderPresentation.MASCULINE),
                version = json.optInt("version", 1)
            )
            AvatarCache.update(config)
            config
        } catch (e: Exception) {
            val fallback = AvatarConfig.DEFAULT_MASCULINE
            AvatarCache.update(fallback)
            fallback
        }
    }

    fun resetAvatar() {
        prefs.edit().remove(KEY_AVATAR_JSON).remove(KEY_IS_GENERATED).apply()
        AvatarCache.clear()
    }

    private inline fun <reified T : Enum<T>> safeValueOf(value: String?, default: T): T {
        if (value.isNullOrEmpty()) return default
        return try {
            java.lang.Enum.valueOf(T::class.java, value)
        } catch (e: Exception) {
            default
        }
    }

    companion object {
        private const val PREFS_NAME = "atlas_avatar_storage"
        private const val KEY_AVATAR_JSON = "key_avatar_config_json"
        private const val KEY_IS_GENERATED = "key_is_avatar_generated"
    }
}
