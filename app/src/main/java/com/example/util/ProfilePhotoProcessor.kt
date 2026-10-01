package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.media.FaceDetector
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

/**
 * Intelligent profile photo processor.
 *
 * Requirements:
 * - Square crop
 * - Automatically center the face using FaceDetector
 * - Compress for optimal memory usage and 60 FPS launcher performance (512x512 JPEG)
 * - Persist locally to internal files storage
 */
object ProfilePhotoProcessor {

    private const val TARGET_SIZE = 512
    private const val PHOTO_FILE_PREFIX = "profile_photo_"

    /**
     * Processes an image from a content Uri:
     * Reads, auto-detects face, center-crops to square, resizes to 512x512, compresses, and saves locally.
     * Returns the absolute path of the saved file.
     */
    suspend fun processAndSavePhoto(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open stream for Uri: $uri")

        // 1. Decode bounds first to calculate inSampleSize
        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, boundsOptions)
        inputStream.close()

        val origW = boundsOptions.outWidth
        val origH = boundsOptions.outHeight
        if (origW <= 0 || origH <= 0) {
            throw IllegalArgumentException("Invalid image dimensions")
        }

        // Calculate sample size to avoid OOM on large camera captures
        var inSampleSize = 1
        val maxDim = max(origW, origH)
        while (maxDim / inSampleSize > 1600) {
            inSampleSize *= 2
        }

        // 2. Decode the downscaled bitmap in 565 format for face detection or ARGB_8888
        val decodeStream = context.contentResolver.openInputStream(uri)
        val decodeOptions = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
            inPreferredConfig = Bitmap.Config.RGB_565 // Required for FaceDetector
        }
        val decodedBitmap = BitmapFactory.decodeStream(decodeStream, null, decodeOptions)
        decodeStream?.close()

        if (decodedBitmap == null) {
            throw IllegalStateException("Failed to decode bitmap from stream")
        }

        val processedBitmap = cropAndCenterFace(decodedBitmap)
        if (processedBitmap != decodedBitmap) {
            decodedBitmap.recycle()
        }

        // Resize to uniform 512x512
        val finalBitmap = if (processedBitmap.width != TARGET_SIZE || processedBitmap.height != TARGET_SIZE) {
            val scaled = Bitmap.createScaledBitmap(processedBitmap, TARGET_SIZE, TARGET_SIZE, true)
            if (scaled != processedBitmap) {
                processedBitmap.recycle()
            }
            scaled
        } else {
            processedBitmap
        }

        // Clean up previous profile photos in filesDir
        cleanupOldPhotos(context)

        // Save to private files directory
        val photoFile = File(context.filesDir, "${PHOTO_FILE_PREFIX}${System.currentTimeMillis()}.jpg")
        FileOutputStream(photoFile).use { out ->
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
        }
        finalBitmap.recycle()

        photoFile.absolutePath
    }

    /**
     * Processes a direct Bitmap from camera preview:
     * Auto-detects face, center-crops to square, resizes to 512x512, compresses, and saves locally.
     * Returns the absolute path of the saved file.
     */
    suspend fun processAndSaveBitmap(context: Context, bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        // Ensure 565 or ARGB_8888
        val workingBitmap = if (bitmap.config != Bitmap.Config.RGB_565) {
            bitmap.copy(Bitmap.Config.RGB_565, true) ?: bitmap
        } else {
            bitmap
        }

        val processedBitmap = cropAndCenterFace(workingBitmap)
        if (processedBitmap != workingBitmap && workingBitmap != bitmap) {
            workingBitmap.recycle()
        }

        val finalBitmap = if (processedBitmap.width != TARGET_SIZE || processedBitmap.height != TARGET_SIZE) {
            val scaled = Bitmap.createScaledBitmap(processedBitmap, TARGET_SIZE, TARGET_SIZE, true)
            if (scaled != processedBitmap) {
                processedBitmap.recycle()
            }
            scaled
        } else {
            processedBitmap
        }

        cleanupOldPhotos(context)

        val photoFile = File(context.filesDir, "${PHOTO_FILE_PREFIX}${System.currentTimeMillis()}.jpg")
        FileOutputStream(photoFile).use { out ->
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
        }
        finalBitmap.recycle()

        photoFile.absolutePath
    }

    /**
     * Detects face in bitmap and crops a square centered on the face.
     * If no face is detected, crops the exact center square of the bitmap.
     */
    private fun cropAndCenterFace(src: Bitmap): Bitmap {
        val w = src.width
        val h = src.height
        val minDim = min(w, h)

        var centerX = w / 2f
        var centerY = h / 2f

        try {
            // FaceDetector requires even width and RGB_565 config
            val safeBmp = if (w % 2 != 0) {
                Bitmap.createBitmap(src, 0, 0, w - 1, h)
            } else {
                src
            }

            val detector = FaceDetector(safeBmp.width, safeBmp.height, 1)
            val faces = arrayOfNulls<FaceDetector.Face>(1)
            val count = detector.findFaces(safeBmp, faces)

            if (count > 0 && faces[0] != null) {
                val face = faces[0]!!
                val midPoint = PointF()
                face.getMidPoint(midPoint)
                // Center slightly above eyes for aesthetic portrait balance
                centerX = midPoint.x
                centerY = midPoint.y - (face.eyesDistance() * 0.25f)
            }

            if (safeBmp != src) {
                safeBmp.recycle()
            }
        } catch (_: Exception) {
            // Fallback to center crop if detector fails
            centerX = w / 2f
            centerY = h / 2f
        }

        // Calculate square crop bounds centered on (centerX, centerY)
        val halfSide = minDim / 2f
        var left = (centerX - halfSide).toInt()
        var top = (centerY - halfSide).toInt()

        // Clamp to bitmap boundaries
        if (left < 0) left = 0
        if (top < 0) top = 0
        if (left + minDim > w) left = w - minDim
        if (top + minDim > h) top = h - minDim

        return Bitmap.createBitmap(src, left, top, minDim, minDim)
    }

    private fun cleanupOldPhotos(context: Context) {
        try {
            val dir = context.filesDir
            dir.listFiles { _, name -> name.startsWith(PHOTO_FILE_PREFIX) && name.endsWith(".jpg") }
                ?.forEach { it.delete() }
        } catch (_: Exception) {}
    }
}
