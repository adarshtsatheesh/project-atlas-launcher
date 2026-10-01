package com.example.avatar

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Intelligent avatar generator that maps facial geometry, skin tone, hair color,
 * and beard characteristics from an uploaded photo into a stylized digital character.
 */
object AvatarGenerator {

    /**
     * Analyzes selfie bitmap and produces a personalized stylized AvatarConfig.
     */
    fun generateFromSelfie(bitmap: Bitmap): AvatarConfig {
        try {
            val w = bitmap.width
            val h = bitmap.height
            if (w <= 10 || h <= 10) return AvatarConfig.DEFAULT_MASCULINE

            // 1. Sample central face region for skin tone
            val skinTone = detectSkinTone(bitmap, w, h)

            // 2. Sample top head region for hair color and hair volume
            val (hairColor, hairStyle) = detectHair(bitmap, w, h)

            // 3. Sample lower face / chin region for beard presence
            val beardStyle = detectBeard(bitmap, w, h, skinTone)

            // 4. Sample eye area for eye color
            val eyeColor = detectEyeColor(bitmap, w, h)

            // 5. Detect face proportions / face shape
            val faceShape = detectFaceShape(w, h)

            // 6. Heuristic for gender presentation
            val isLongHair = hairStyle == HairStyle.LONG_WAVY || hairStyle == HairStyle.HIGH_PONYTAIL
            val genderPresentation = if (isLongHair && beardStyle == BeardStyle.CLEAN_SHAVEN) {
                GenderPresentation.FEMININE
            } else {
                GenderPresentation.MASCULINE
            }

            return AvatarConfig(
                skinTone = skinTone,
                faceShape = faceShape,
                hairStyle = hairStyle,
                hairColor = hairColor,
                eyeShape = EyeShape.ALMOND_FOCUSED,
                eyeColor = eyeColor,
                eyebrowStyle = if (genderPresentation == GenderPresentation.FEMININE) EyebrowStyle.SHARP_ARCHED else EyebrowStyle.CLEAN_NATURAL,
                beardStyle = beardStyle,
                beardColor = hairColor,
                outfitType = OutfitType.MINIMAL_HOODIE,
                outfitPrimaryColor = 0xFF141A29,
                outfitSecondaryColor = 0xFF00C8FF,
                pantsType = PantsType.TAILORED_JOGGERS,
                pantsColor = 0xFF0F131D,
                footwear = FootwearType.MINIMAL_SNEAKERS,
                footwearColor = 0xFFE2E8F0,
                glasses = GlassesType.NONE,
                watch = WatchType.NONE,
                hat = HatType.NONE,
                genderPresentation = genderPresentation
            )
        } catch (e: Exception) {
            return AvatarConfig.DEFAULT_MASCULINE
        }
    }

    private fun detectSkinTone(bitmap: Bitmap, w: Int, h: Int): SkinTone {
        var rSum = 0L
        var gSum = 0L
        var bSum = 0L
        var count = 0

        // Sample nose/cheek center box (x: 42%..58%, y: 40%..55%)
        val startX = (w * 0.42f).toInt()
        val endX = (w * 0.58f).toInt()
        val startY = (h * 0.40f).toInt()
        val endY = (h * 0.55f).toInt()

        val step = maxOf(1, (endX - startX) / 10)

        for (y in startY until endY step step) {
            for (x in startX until endX step step) {
                val px = bitmap.getPixel(x, y)
                rSum += AndroidColor.red(px)
                gSum += AndroidColor.green(px)
                bSum += AndroidColor.blue(px)
                count++
            }
        }

        if (count == 0) return SkinTone.WARM_IVORY

        val avgR = (rSum / count).toInt()
        val avgG = (gSum / count).toInt()
        val avgB = (bSum / count).toInt()

        // Match closest SkinTone enum by Euclidean RGB distance
        var bestMatch = SkinTone.WARM_IVORY
        var minDistance = Double.MAX_VALUE

        for (tone in SkinTone.entries) {
            val tr = (tone.baseColor.red * 255).toInt()
            val tg = (tone.baseColor.green * 255).toInt()
            val tb = (tone.baseColor.blue * 255).toInt()

            val dist = sqrt(
                ((avgR - tr) * (avgR - tr) +
                 (avgG - tg) * (avgG - tg) +
                 (avgB - tb) * (avgB - tb)).toDouble()
            )

            if (dist < minDistance) {
                minDistance = dist
                bestMatch = tone
            }
        }

        return bestMatch
    }

    private fun detectHair(bitmap: Bitmap, w: Int, h: Int): Pair<HairColor, HairStyle> {
        var rSum = 0L
        var gSum = 0L
        var bSum = 0L
        var count = 0

        // Top 10% to 24% of height, center 50% width
        val startX = (w * 0.25f).toInt()
        val endX = (w * 0.75f).toInt()
        val startY = (h * 0.08f).toInt()
        val endY = (h * 0.22f).toInt()

        val step = maxOf(1, (endX - startX) / 12)

        for (y in startY until endY step step) {
            for (x in startX until endX step step) {
                val px = bitmap.getPixel(x, y)
                rSum += AndroidColor.red(px)
                gSum += AndroidColor.green(px)
                bSum += AndroidColor.blue(px)
                count++
            }
        }

        val avgR = if (count > 0) (rSum / count).toInt() else 35
        val avgG = if (count > 0) (gSum / count).toInt() else 30
        val avgB = if (count > 0) (bSum / count).toInt() else 28

        val lum = 0.299 * avgR + 0.587 * avgG + 0.114 * avgB

        val matchedColor = when {
            lum > 165 -> HairColor.HONEY_BLONDE
            lum > 130 -> HairColor.PLATINUM
            avgR > avgB + 28 && avgR > avgG + 20 -> HairColor.AUBURN_RED
            lum > 85 -> HairColor.WARM_CHESTNUT
            lum > 48 -> HairColor.DEEP_BROWN
            else -> HairColor.JET_BLACK
        }

        // Side hair sample to test for long hair vs short fade
        val sideY = (h * 0.45f).toInt()
        val leftX = (w * 0.15f).toInt()
        val rightX = (w * 0.85f).toInt()

        var hasLongSideHair = false
        if (leftX in 0 until w && sideY in 0 until h && rightX in 0 until w) {
            val leftPx = bitmap.getPixel(leftX, sideY)
            val rightPx = bitmap.getPixel(rightX, sideY)
            val leftLum = 0.299 * AndroidColor.red(leftPx) + 0.587 * AndroidColor.green(leftPx) + 0.114 * AndroidColor.blue(leftPx)
            val rightLum = 0.299 * AndroidColor.red(rightPx) + 0.587 * AndroidColor.green(rightPx) + 0.114 * AndroidColor.blue(rightPx)

            if (abs(leftLum - lum) < 35 && abs(rightLum - lum) < 35) {
                hasLongSideHair = true
            }
        }

        val matchedStyle = if (hasLongSideHair) {
            HairStyle.LONG_WAVY
        } else {
            HairStyle.SHORT_FADE
        }

        return Pair(matchedColor, matchedStyle)
    }

    private fun detectBeard(bitmap: Bitmap, w: Int, h: Int, skinTone: SkinTone): BeardStyle {
        // Sample chin / jaw area: y 68%..82%, x 35%..65%
        var beardLumSum = 0.0
        var count = 0

        val startX = (w * 0.35f).toInt()
        val endX = (w * 0.65f).toInt()
        val startY = (h * 0.68f).toInt()
        val endY = (h * 0.82f).toInt()
        val step = maxOf(1, (endX - startX) / 8)

        for (y in startY until endY step step) {
            for (x in startX until endX step step) {
                val px = bitmap.getPixel(x, y)
                val lum = 0.299 * AndroidColor.red(px) + 0.587 * AndroidColor.green(px) + 0.114 * AndroidColor.blue(px)
                beardLumSum += lum
                count++
            }
        }

        val chinLum = if (count > 0) beardLumSum / count else 120.0
        val skinLum = 0.299 * (skinTone.baseColor.red * 255) + 0.587 * (skinTone.baseColor.green * 255) + 0.114 * (skinTone.baseColor.blue * 255)

        val diff = skinLum - chinLum

        return when {
            diff > 65.0 -> BeardStyle.FULL_BEARD
            diff > 42.0 -> BeardStyle.SHORT_BOXED
            diff > 22.0 -> BeardStyle.LIGHT_STUBBLE
            else -> BeardStyle.CLEAN_SHAVEN
        }
    }

    private fun detectEyeColor(bitmap: Bitmap, w: Int, h: Int): EyeColor {
        // Center eye region approx 38% height
        val eyeY = (h * 0.38f).toInt()
        val eyeLeftX = (w * 0.38f).toInt()

        if (eyeLeftX in 0 until w && eyeY in 0 until h) {
            val px = bitmap.getPixel(eyeLeftX, eyeY)
            val r = AndroidColor.red(px)
            val g = AndroidColor.green(px)
            val b = AndroidColor.blue(px)

            if (b > r + 15 && b > g + 10) return EyeColor.AZURE_BLUE
            if (g > r + 10 && g > b) return EyeColor.EMERALD_GREEN
            if (r > 120 && g > 90 && b < 60) return EyeColor.WARM_HAZEL
        }
        return EyeColor.DARK_BROWN
    }

    private fun detectFaceShape(w: Int, h: Int): FaceShape {
        val aspect = h.toFloat() / maxOf(1, w).toFloat()
        return when {
            aspect > 1.45f -> FaceShape.OVAL_BALANCED
            aspect < 1.25f -> FaceShape.ROUND_SOFT
            else -> FaceShape.SQUARE_STRONG
        }
    }
}
