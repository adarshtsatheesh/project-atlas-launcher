package com.example.ui.appearance

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.domain.model.ColorPalette
import com.example.ui.theme.LevelThemeColors
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary

/**
 * Enterprise-grade Theme Manager for the Atlas Launcher & Appearance Studio.
 * Handles dynamic color schemes, glassmorphic brushes, AMOLED void modes,
 * and live accent color mapping.
 */
object ThemeManager {

    fun getColorScheme(
        themeMode: ThemeMode,
        accentColor: Color,
        isSystemInDarkTheme: Boolean = true
    ): ColorScheme {
        return when (themeMode) {
            ThemeMode.LIGHT -> {
                lightColorScheme(
                    primary = accentColor,
                    onPrimary = Color.White,
                    primaryContainer = accentColor.copy(alpha = 0.15f),
                    onPrimaryContainer = Color(0xFF0F172A),
                    secondary = Color(0xFF334155),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFF1F5F9),
                    onSecondaryContainer = Color(0xFF0F172A),
                    tertiary = accentColor,
                    onTertiary = Color.White,
                    background = Color(0xFFF8FAFC),
                    onBackground = Color(0xFF0F172A),
                    surface = Color(0xFFFFFFFF),
                    onSurface = Color(0xFF0F172A),
                    surfaceVariant = Color(0xFFF1F5F9),
                    onSurfaceVariant = Color(0xFF475569),
                    outline = Color(0xFFCBD5E1),
                    outlineVariant = Color(0xFFE2E8F0)
                )
            }
            ThemeMode.AMOLED_BLACK -> {
                darkColorScheme(
                    primary = accentColor,
                    onPrimary = TextPureWhite,
                    primaryContainer = accentColor.copy(alpha = 0.20f),
                    onPrimaryContainer = TextPureWhite,
                    secondary = TextPureWhite,
                    onSecondary = Color.Black,
                    secondaryContainer = Color(0xFF0C0E12),
                    onSecondaryContainer = TextSecondary,
                    tertiary = accentColor,
                    onTertiary = Color.Black,
                    background = Color.Black,
                    onBackground = TextPureWhite,
                    surface = Color(0xFF060608),
                    onSurface = TextPureWhite,
                    surfaceVariant = Color(0xFF0E0E12),
                    onSurfaceVariant = TextSecondary,
                    outline = Color(0x28FFFFFF),
                    outlineVariant = Color(0x14FFFFFF)
                )
            }
            ThemeMode.DARK -> {
                darkColorScheme(
                    primary = accentColor,
                    onPrimary = TextPureWhite,
                    primaryContainer = accentColor.copy(alpha = 0.20f),
                    onPrimaryContainer = TextPureWhite,
                    secondary = TextPureWhite,
                    onSecondary = Color(0xFF0A0C10),
                    secondaryContainer = Color(0xFF141820),
                    onSecondaryContainer = TextSecondary,
                    tertiary = accentColor,
                    onTertiary = Color(0xFF0A0C10),
                    background = Color(0xFF0A0C10),
                    onBackground = TextPureWhite,
                    surface = Color(0xFF10131A),
                    onSurface = TextPureWhite,
                    surfaceVariant = Color(0xFF1A1F2B),
                    onSurfaceVariant = TextSecondary,
                    outline = Color(0x30FFFFFF),
                    outlineVariant = Color(0x18FFFFFF)
                )
            }
            ThemeMode.SYSTEM -> {
                if (isSystemInDarkTheme) {
                    getColorScheme(ThemeMode.AMOLED_BLACK, accentColor, true)
                } else {
                    getColorScheme(ThemeMode.LIGHT, accentColor, false)
                }
            }
            ThemeMode.CUSTOM -> {
                darkColorScheme(
                    primary = accentColor,
                    onPrimary = TextPureWhite,
                    primaryContainer = accentColor.copy(alpha = 0.22f),
                    onPrimaryContainer = TextPureWhite,
                    secondary = TextPureWhite,
                    onSecondary = Color.Black,
                    secondaryContainer = Color(0xFF121018),
                    onSecondaryContainer = TextSecondary,
                    tertiary = accentColor,
                    onTertiary = Color.Black,
                    background = Color(0xFF050508),
                    onBackground = TextPureWhite,
                    surface = Color(0xFF0D0B14),
                    onSurface = TextPureWhite,
                    surfaceVariant = Color(0xFF181524),
                    onSurfaceVariant = TextSecondary,
                    outline = accentColor.copy(alpha = 0.35f),
                    outlineVariant = Color(0x18FFFFFF)
                )
            }
        }
    }

    /**
     * Builds specular glassmorphic background brush reflecting transparency and accent.
     */
    fun getCardBackgroundBrush(
        accentColor: Color,
        cardTransparency: Int = 15,
        themeMode: ThemeMode = ThemeMode.AMOLED_BLACK
    ): Brush {
        val alphaBase = (cardTransparency / 100f).coerceIn(0.05f, 0.90f)
        val isLight = themeMode == ThemeMode.LIGHT
        return if (isLight) {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 1f - alphaBase * 0.4f),
                    Color(0xFFF1F5F9).copy(alpha = 1f - alphaBase * 0.4f)
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF12141A).copy(alpha = alphaBase + 0.15f),
                    Color(0xFF07080C).copy(alpha = alphaBase + 0.10f)
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        }
    }

    /**
     * Builds subtle glowing border brush with specular light edge.
     */
    fun getBorderBrush(accentColor: Color): Brush {
        return Brush.linearGradient(
            colors = listOf(
                Color(0x55FFFFFF),
                Color(0x1AFFFFFF),
                accentColor.copy(alpha = 0.50f)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    /**
     * Creates matching [LevelThemeColors] dynamically from any accent color.
     */
    fun getLevelThemeColors(accentColor: Color, themeMode: ThemeMode = ThemeMode.AMOLED_BLACK): LevelThemeColors {
        val pri = accentColor
        val sec = accentColor.copy(alpha = 0.85f)
        val deep = accentColor.copy(alpha = 0.55f)
        val isLight = themeMode == ThemeMode.LIGHT

        return if (isLight) {
            LevelThemeColors(
                primary = pri,
                secondary = sec,
                deep = deep,
                cardBackground = Color(0xFFFFFFFF),
                cardBorder = pri.copy(alpha = 0.35f),
                progressBarBrush = Brush.horizontalGradient(listOf(deep, pri, sec)),
                cardBorderBrush = Brush.linearGradient(listOf(pri.copy(alpha = 0.5f), Color(0x33000000), pri.copy(alpha = 0.3f))),
                screenBackground = Color(0xFFF8FAFC),
                cardElevated = Color(0xFFF1F5F9),
                trackBackground = Color(0xFFE2E8F0),
                innerSurface = Color(0xFFF8FAFC),
                accentGlow = pri
            )
        } else {
            LevelThemeColors(
                primary = pri,
                secondary = sec,
                deep = deep,
                cardBackground = Color(0xFF090B10),
                cardBorder = pri.copy(alpha = 0.30f),
                progressBarBrush = Brush.horizontalGradient(listOf(deep, pri, sec)),
                cardBorderBrush = Brush.linearGradient(listOf(pri.copy(alpha = 0.55f), Color(0x18FFFFFF), pri.copy(alpha = 0.25f))),
                screenBackground = if (themeMode == ThemeMode.AMOLED_BLACK) Color.Black else Color(0xFF07090E),
                cardElevated = Color(0xFF11141E),
                trackBackground = Color(0xFF151924),
                innerSurface = Color(0xFF0D1018),
                accentGlow = sec
            )
        }
    }
}
