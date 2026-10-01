package com.example.avatar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

/**
 * Main engine orchestrator for the Avatar System.
 * Coordinates generation from camera/gallery, local persistence, in-memory caching,
 * and live customization updates.
 */
class AvatarEngine(private val context: Context) {

    private val storage = AvatarStorage(context)

    val currentAvatar: StateFlow<AvatarConfig> = AvatarCache.avatarState

    init {
        // Hydrate from storage on initialization
        storage.loadAvatar()
    }

    fun hasGeneratedAvatar(): Boolean {
        return storage.hasSavedAvatar()
    }

    suspend fun generateFromUri(uri: Uri): AvatarConfig = withContext(Dispatchers.IO) {
        val bitmap = try {
            val stream: InputStream? = context.contentResolver.openInputStream(uri)
            val opts = BitmapFactory.Options().apply {
                inSampleSize = 2
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            BitmapFactory.decodeStream(stream, null, opts)
        } catch (e: Exception) {
            null
        }

        val config = if (bitmap != null) {
            val gen = AvatarGenerator.generateFromSelfie(bitmap)
            bitmap.recycle()
            gen
        } else {
            AvatarConfig.DEFAULT_MASCULINE
        }

        storage.saveAvatar(config)
        config
    }

    fun generateDefault(gender: GenderPresentation = GenderPresentation.MASCULINE): AvatarConfig {
        val base = if (gender == GenderPresentation.FEMININE) {
            AvatarConfig.DEFAULT_FEMININE
        } else {
            AvatarConfig.DEFAULT_MASCULINE
        }
        storage.saveAvatar(base)
        return base
    }

    fun updateConfig(config: AvatarConfig) {
        storage.saveAvatar(config)
    }

    fun resetAvatar() {
        storage.resetAvatar()
    }
}
