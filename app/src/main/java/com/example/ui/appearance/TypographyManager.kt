package com.example.ui.appearance

import androidx.compose.material3.Typography
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.concurrent.ConcurrentHashMap

/**
 * High-performance Typography Manager.
 * Caches FontFamilies and Material 3 Typography instances to eliminate allocation
 * overhead on frame transitions and recompositions.
 */
object TypographyManager {

    private val fontFamilyCache = ConcurrentHashMap<AppFontFamily, FontFamily>()
    private val typographyCache = ConcurrentHashMap<AppFontFamily, Typography>()

    /**
     * Retrieves or builds the [FontFamily] backed by bundled static TTF fonts.
     */
    fun getFontFamily(font: AppFontFamily): FontFamily {
        return fontFamilyCache.getOrPut(font) {
            FontFamily(
                Font(font.fontResId, FontWeight.Normal),
                Font(font.fontResId, FontWeight.Medium),
                Font(font.fontResId, FontWeight.SemiBold),
                Font(font.fontResId, FontWeight.Bold)
            )
        }
    }

    /**
     * Builds and caches a full Material Design 3 [Typography] configuration
     * where all scales inherit the chosen typeface.
     */
    fun getTypography(font: AppFontFamily): Typography {
        return typographyCache.getOrPut(font) {
            val family = getFontFamily(font)
            Typography(
                displayLarge = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Light,
                    fontSize = 58.sp,
                    lineHeight = 64.sp,
                    letterSpacing = (-1.0).sp
                ),
                displayMedium = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Normal,
                    fontSize = 42.sp,
                    lineHeight = 48.sp,
                    letterSpacing = (-0.5).sp
                ),
                displaySmall = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Normal,
                    fontSize = 34.sp,
                    lineHeight = 40.sp,
                    letterSpacing = 0.sp
                ),
                headlineLarge = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    letterSpacing = (-0.4).sp
                ),
                headlineMedium = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    letterSpacing = (-0.2).sp
                ),
                headlineSmall = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    letterSpacing = 0.sp
                ),
                titleLarge = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.sp
                ),
                titleMedium = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.15.sp
                ),
                titleSmall = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp,
                    letterSpacing = 0.1.sp
                ),
                bodyLarge = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    letterSpacing = 0.2.sp
                ),
                bodyMedium = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    letterSpacing = 0.2.sp
                ),
                bodySmall = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.3.sp
                ),
                labelLarge = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    letterSpacing = 0.4.sp
                ),
                labelMedium = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    letterSpacing = 0.5.sp
                ),
                labelSmall = TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 0.8.sp
                )
            )
        }
    }
}

/**
 * CompositionLocal to expose the active font family cleanly anywhere in the Compose tree.
 */
val LocalAppFontFamily = compositionLocalOf {
    TypographyManager.getFontFamily(AppFontFamily.SPACE_GROTESK)
}
