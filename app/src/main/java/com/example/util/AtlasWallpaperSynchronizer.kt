package com.example.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.domain.model.BuiltInWallpaper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Which system surfaces the launcher wallpaper should be applied to.
 */
enum class WallpaperTarget { HOME, LOCK, BOTH }

/**
 * High-performance System Wallpaper Synchronizer.
 *
 * Ensures that Android's SystemUI Recents (Overview) screen, Quickstep navigation,
 * home screen and lock screen seamlessly reflect the launcher's active wallpaper,
 * eliminating visual mismatches or abrupt background jumps when entering/exiting Recent Apps.
 */
object AtlasWallpaperSynchronizer {

    private const val PREFS_NAME = "atlas_wallpaper_sync"
    private const val KEY_LAST_SYNCED = "last_synced_wallpaper_id"

    /**
     * Synchronizes the launcher's active wallpaper to Android's [WallpaperManager].
     * Runs asynchronously on [Dispatchers.IO] to avoid any main thread frame drops.
     *
     * @param target which wallpaper surface(s) to update (home, lock, or both).
     */
    suspend fun syncWallpaper(
        context: Context,
        wallpaper: BuiltInWallpaper,
        customImagePath: String? = null,
        force: Boolean = false,
        target: WallpaperTarget = WallpaperTarget.BOTH,
        lockWallpaper: BuiltInWallpaper? = null,
        lockCustomImagePath: String? = null
    ) = withContext(Dispatchers.IO) {
        val syncKey = "${wallpaper.name}_${customImagePath ?: ""}_${target.name}_${lockWallpaper?.name ?: ""}_${lockCustomImagePath ?: ""}"
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastSynced = prefs.getString(KEY_LAST_SYNCED, null)

        if (!force && lastSynced == syncKey) {
            // Already synchronized to Android WallpaperManager
            return@withContext
        }

        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            if (!wallpaperManager.isWallpaperSupported) return@withContext

            val displayMetrics = getScreenDimensions(context)
            val width = displayMetrics.widthPixels.coerceAtLeast(1080)
            val height = displayMetrics.heightPixels.coerceAtLeast(1920)

            // Home / system surface (Recents, Quickstep, home screen)
            if (target != WallpaperTarget.LOCK) {
                val bitmap = if (wallpaper == BuiltInWallpaper.CUSTOM_GALLERY && !customImagePath.isNullOrEmpty()) {
                    generateCustomWallpaperBitmap(customImagePath, width, height)
                } else {
                    generateProceduralWallpaperBitmap(wallpaper, width, height)
                }
                if (bitmap != null) {
                    try {
                        wallpaperManager.setBitmap(
                            bitmap,
                            null,
                            true,
                            WallpaperManager.FLAG_SYSTEM
                        )
                    } finally {
                        bitmap.recycle()
                    }
                }
            }

            // Lock screen surface (falls back to the home wallpaper when no dedicated
            // lock wallpaper has been chosen → "Same wallpaper" behavior).
            if (target != WallpaperTarget.HOME) {
                val lockWp = lockWallpaper ?: wallpaper
                val lockPath = if (lockWallpaper == null) customImagePath else lockCustomImagePath
                val lockBitmap = if (lockWp == BuiltInWallpaper.CUSTOM_GALLERY && !lockPath.isNullOrEmpty()) {
                    generateCustomWallpaperBitmap(lockPath, width, height)
                } else {
                    generateProceduralWallpaperBitmap(lockWp, width, height)
                }
                if (lockBitmap != null) {
                    try {
                        wallpaperManager.setBitmap(
                            lockBitmap,
                            null,
                            true,
                            WallpaperManager.FLAG_LOCK
                        )
                    } finally {
                        lockBitmap.recycle()
                    }
                }
            }

            prefs.edit().putString(KEY_LAST_SYNCED, syncKey).apply()
        } catch (e: Exception) {
            // Gracefully catch security / I/O exceptions on restricted OEM firmware
        }
    }

    private fun getScreenDimensions(context: Context): DisplayMetrics {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val metrics = DisplayMetrics()
        wm?.defaultDisplay?.getRealMetrics(metrics)
        return metrics
    }

    /**
     * Renders a high-resolution bitmap replicating the procedural shader backdrops
     * of Atlas Launcher for system-wide wallpaper and Recents continuity.
     */
    fun generateProceduralWallpaperBitmap(wallpaper: BuiltInWallpaper, width: Int, height: Int): Bitmap? {
        return try {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val w = width.toFloat()
            val h = height.toFloat()
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            when (wallpaper) {
                BuiltInWallpaper.OBSIDIAN_VOID -> {
                    // 1. Signature Obsidian Horizon
                    paint.shader = LinearGradient(
                        0f, 0f, 0f, h,
                        intArrayOf(Color.parseColor("#070709"), Color.parseColor("#0A0A0E"), Color.parseColor("#0F0E14"), Color.parseColor("#08070B")),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawRect(0f, 0f, w, h, paint)

                    // Ambient Orbs
                    paint.shader = RadialGradient(
                        w * 0.85f, h * 0.20f, w * 0.80f,
                        intArrayOf(Color.argb(102, 255, 42, 77), Color.argb(56, 198, 26, 54), Color.TRANSPARENT),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawCircle(w * 0.85f, h * 0.20f, w * 0.80f, paint)

                    paint.shader = RadialGradient(
                        w * 0.15f, h * 0.82f, w * 0.70f,
                        intArrayOf(Color.argb(77, 229, 9, 20), Color.argb(40, 128, 10, 29), Color.TRANSPARENT),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawCircle(w * 0.15f, h * 0.82f, w * 0.70f, paint)

                    // Ridge
                    val ridge = Path().apply {
                        moveTo(0f, h * 0.52f)
                        cubicTo(w * 0.25f, h * 0.46f, w * 0.65f, h * 0.58f, w, h * 0.48f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    paint.shader = LinearGradient(0f, h * 0.46f, 0f, h * 0.80f, Color.argb(34, 31, 16, 26), Color.argb(51, 18, 8, 16), Shader.TileMode.CLAMP)
                    canvas.drawPath(ridge, paint)
                }

                BuiltInWallpaper.CYBER_EMERALD -> {
                    // 2. Cyber Emerald Aurora
                    paint.shader = LinearGradient(
                        0f, 0f, 0f, h,
                        intArrayOf(Color.parseColor("#040A07"), Color.parseColor("#06120D"), Color.parseColor("#051710"), Color.parseColor("#030806")),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawRect(0f, 0f, w, h, paint)

                    paint.shader = RadialGradient(
                        w * 0.20f, h * 0.25f, w * 0.85f,
                        intArrayOf(Color.argb(85, 16, 185, 129), Color.argb(40, 5, 150, 105), Color.TRANSPARENT),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawCircle(w * 0.20f, h * 0.25f, w * 0.85f, paint)

                    paint.shader = RadialGradient(
                        w * 0.80f, h * 0.75f, w * 0.70f,
                        intArrayOf(Color.argb(60, 20, 184, 166), Color.argb(25, 13, 148, 136), Color.TRANSPARENT),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawCircle(w * 0.80f, h * 0.75f, w * 0.70f, paint)
                }

                BuiltInWallpaper.ELECTRIC_AZURE -> {
                    // 3. Azure Deep Horizon
                    paint.shader = LinearGradient(
                        0f, 0f, 0f, h,
                        intArrayOf(Color.parseColor("#030712"), Color.parseColor("#050F1E"), Color.parseColor("#081426"), Color.parseColor("#02050D")),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawRect(0f, 0f, w, h, paint)

                    paint.shader = RadialGradient(
                        w * 0.75f, h * 0.28f, w * 0.85f,
                        intArrayOf(Color.argb(80, 2, 132, 199), Color.argb(35, 3, 105, 161), Color.TRANSPARENT),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawCircle(w * 0.75f, h * 0.28f, w * 0.85f, paint)
                }

                BuiltInWallpaper.AMBER_ECLIPSE -> {
                    // 4. Amber Dusk Eclipse
                    paint.shader = LinearGradient(
                        0f, 0f, 0f, h,
                        intArrayOf(Color.parseColor("#0C0804"), Color.parseColor("#120B05"), Color.parseColor("#160E06"), Color.parseColor("#080502")),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawRect(0f, 0f, w, h, paint)

                    paint.shader = RadialGradient(
                        w * 0.50f, h * 0.35f, w * 0.80f,
                        intArrayOf(Color.argb(80, 245, 158, 11), Color.argb(35, 180, 83, 9), Color.TRANSPARENT),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawCircle(w * 0.50f, h * 0.35f, w * 0.80f, paint)
                }

                BuiltInWallpaper.VIOLET_TWILIGHT -> {
                    // 5. Violet Twilight
                    paint.shader = LinearGradient(
                        0f, 0f, 0f, h,
                        intArrayOf(Color.parseColor("#080511"), Color.parseColor("#0E071A"), Color.parseColor("#120A20"), Color.parseColor("#05030A")),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawRect(0f, 0f, w, h, paint)

                    paint.shader = RadialGradient(
                        w * 0.30f, h * 0.30f, w * 0.85f,
                        intArrayOf(Color.argb(80, 168, 85, 247), Color.argb(35, 126, 34, 206), Color.TRANSPARENT),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawCircle(w * 0.30f, h * 0.30f, w * 0.85f, paint)
                }

                BuiltInWallpaper.STEALTH_OLED -> {
                    // 6. Stealth Carbon OLED
                    paint.shader = LinearGradient(
                        0f, 0f, 0f, h,
                        intArrayOf(Color.parseColor("#000000"), Color.parseColor("#040404"), Color.parseColor("#070707"), Color.parseColor("#000000")),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawRect(0f, 0f, w, h, paint)

                    paint.shader = RadialGradient(
                        w * 0.50f, h * 0.50f, w * 0.70f,
                        intArrayOf(Color.argb(25, 255, 255, 255), Color.argb(8, 255, 255, 255), Color.TRANSPARENT),
                        null, Shader.TileMode.CLAMP
                    )
                    canvas.drawCircle(w * 0.50f, h * 0.50f, w * 0.70f, paint)
                }

                BuiltInWallpaper.CUSTOM_GALLERY -> {
                    // Fallback to obsidian void
                    return generateProceduralWallpaperBitmap(BuiltInWallpaper.OBSIDIAN_VOID, width, height)
                }
            }

            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun generateCustomWallpaperBitmap(filePath: String, targetWidth: Int, targetHeight: Int): Bitmap? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null

            // Subsample large gallery images to avoid OutOfMemoryError
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(filePath, options)

            var inSampleSize = 1
            if (options.outHeight > targetHeight || options.outWidth > targetWidth) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while (halfHeight / inSampleSize >= targetHeight && halfWidth / inSampleSize >= targetWidth) {
                    inSampleSize *= 2
                }
            }

            options.inJustDecodeBounds = false
            options.inSampleSize = inSampleSize
            val decoded = BitmapFactory.decodeFile(filePath, options) ?: return null

            // Scale to fill target bounds and draw with subtle scrim
            val scaled = Bitmap.createScaledBitmap(decoded, targetWidth, targetHeight, true)
            if (scaled != decoded) {
                decoded.recycle()
            }

            val canvas = Canvas(scaled)
            val scrimPaint = Paint().apply {
                color = Color.argb(90, 0, 0, 0) // 35% dim scrim for legibility
            }
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), scrimPaint)
            scaled
        } catch (e: Exception) {
            null
        }
    }
}
