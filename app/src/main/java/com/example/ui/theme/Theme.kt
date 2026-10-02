package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.domain.model.ColorPalette
import com.example.ui.appearance.AppearanceState
import com.example.ui.appearance.LocalAppFontFamily
import com.example.ui.appearance.LocalAppearanceState
import com.example.ui.appearance.ThemeManager
import com.example.ui.appearance.TypographyManager

@Composable
fun MyApplicationTheme(
    palette: ColorPalette = ColorPalette.OBSIDIAN_CRIMSON,
    appearance: AppearanceState = AppearanceState(),
    content: @Composable () -> Unit
) {
    val accentColor = remember(appearance.accentPreset, appearance.customAccentHex, palette) {
        appearance.activeAccentColor
    }

    val cardBrush = remember(accentColor, appearance.cardTransparency, appearance.themeMode) {
        ThemeManager.getCardBackgroundBrush(
            accentColor = accentColor,
            cardTransparency = appearance.cardTransparency,
            themeMode = appearance.themeMode
        )
    }

    val borderBrush = remember(accentColor) {
        ThemeManager.getBorderBrush(accentColor)
    }

    val colorScheme = remember(appearance.themeMode, accentColor) {
        ThemeManager.getColorScheme(
            themeMode = appearance.themeMode,
            accentColor = accentColor
        )
    }

    val typography = remember(
        appearance.fontFamily,
        appearance.fontSizeScale,
        appearance.fontLineHeightMultiplier,
        appearance.fontBoldness
    ) {
        TypographyManager.getTypography(appearance)
    }

    val levelThemeColors = remember(accentColor, appearance.themeMode) {
        ThemeManager.getLevelThemeColors(accentColor, appearance.themeMode)
    }

    CompositionLocalProvider(
        LocalAtlasAccent provides accentColor,
        LocalAtlasBorderBrush provides borderBrush,
        LocalAtlasCardBackgroundBrush provides cardBrush,
        LocalLevelThemeColors provides levelThemeColors,
        LocalAppearanceState provides appearance,
        LocalAppFontFamily provides TypographyManager.getFontFamily(appearance.fontFamily)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}

data class LevelThemeColors(
    val primary: Color,
    val secondary: Color,
    val deep: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val progressBarBrush: Brush,
    val cardBorderBrush: Brush,
    val screenBackground: Color = Color(0xFF020407),
    val cardElevated: Color = Color(0xFF0B101E),
    val trackBackground: Color = Color(0xFF101624),
    val innerSurface: Color = Color(0xFF0C111E),
    val accentGlow: Color = secondary
)

fun getLevelThemeColors(palette: ColorPalette): LevelThemeColors {
    return when (palette) {
        ColorPalette.OBSIDIAN_CRIMSON -> {
            val pri = Color(0xFFFF2A4D)
            val sec = Color(0xFFFF6482)
            val deep = Color(0xFF8B0018)
            LevelThemeColors(
                primary = pri,
                secondary = sec,
                deep = deep,
                cardBackground = Color(0xFF10070B),
                cardBorder = Color(0x33FF2A4D),
                progressBarBrush = Brush.horizontalGradient(listOf(deep, pri, sec)),
                cardBorderBrush = Brush.linearGradient(listOf(Color(0x55FF2A4D), Color(0x18FFFFFF), Color(0x22FF2A4D))),
                screenBackground = Color(0xFF060203),
                cardElevated = Color(0xFF1A0A0E),
                trackBackground = Color(0xFF220A10),
                innerSurface = Color(0xFF16070B),
                accentGlow = sec
            )
        }
        ColorPalette.CYBER_EMERALD -> {
            val pri = Color(0xFF10B981)
            val sec = Color(0xFF34D399)
            val deep = Color(0xFF047857)
            LevelThemeColors(
                primary = pri,
                secondary = sec,
                deep = deep,
                cardBackground = Color(0xFF04120C),
                cardBorder = Color(0x3310B981),
                progressBarBrush = Brush.horizontalGradient(listOf(deep, pri, sec)),
                cardBorderBrush = Brush.linearGradient(listOf(Color(0x5510B981), Color(0x18FFFFFF), Color(0x2210B981))),
                screenBackground = Color(0xFF020704),
                cardElevated = Color(0xFF071F14),
                trackBackground = Color(0xFF0A2418),
                innerSurface = Color(0xFF05170F),
                accentGlow = sec
            )
        }
        ColorPalette.ELECTRIC_AZURE -> {
            val pri = Color(0xFF00C8FF)
            val sec = Color(0xFF38BDF8)
            val deep = Color(0xFF0070F3)
            LevelThemeColors(
                primary = pri,
                secondary = sec,
                deep = deep,
                cardBackground = Color(0xFF070B14),
                cardBorder = Color(0x3300C8FF),
                progressBarBrush = Brush.horizontalGradient(listOf(deep, pri, sec)),
                cardBorderBrush = Brush.linearGradient(listOf(Color(0x5500C8FF), Color(0x18FFFFFF), Color(0x2200C8FF))),
                screenBackground = Color(0xFF020407),
                cardElevated = Color(0xFF0B1220),
                trackBackground = Color(0xFF0D1728),
                innerSurface = Color(0xFF08101C),
                accentGlow = sec
            )
        }
        ColorPalette.AMBER_GOLD -> {
            val pri = Color(0xFFF59E0B)
            val sec = Color(0xFFFBBF24)
            val deep = Color(0xFFB45309)
            LevelThemeColors(
                primary = pri,
                secondary = sec,
                deep = deep,
                cardBackground = Color(0xFF120C04),
                cardBorder = Color(0x33F59E0B),
                progressBarBrush = Brush.horizontalGradient(listOf(deep, pri, sec)),
                cardBorderBrush = Brush.linearGradient(listOf(Color(0x55F59E0B), Color(0x18FFFFFF), Color(0x22F59E0B))),
                screenBackground = Color(0xFF070501),
                cardElevated = Color(0xFF1F1507),
                trackBackground = Color(0xFF241708),
                innerSurface = Color(0xFF180F05),
                accentGlow = sec
            )
        }
        ColorPalette.NEON_VIOLET -> {
            val pri = Color(0xFFA855F7)
            val sec = Color(0xFFC084FC)
            val deep = Color(0xFF6B21A8)
            LevelThemeColors(
                primary = pri,
                secondary = sec,
                deep = deep,
                cardBackground = Color(0xFF0E0518),
                cardBorder = Color(0x33A855F7),
                progressBarBrush = Brush.horizontalGradient(listOf(deep, pri, sec)),
                cardBorderBrush = Brush.linearGradient(listOf(Color(0x55A855F7), Color(0x18FFFFFF), Color(0x22A855F7))),
                screenBackground = Color(0xFF05020A),
                cardElevated = Color(0xFF180A28),
                trackBackground = Color(0xFF200B33),
                innerSurface = Color(0xFF140722),
                accentGlow = sec
            )
        }
        ColorPalette.MINIMAL_MONOCHROME -> {
            val pri = Color(0xFFE2E8F0)
            val sec = Color(0xFFFFFFFF)
            val deep = Color(0xFF64748B)
            LevelThemeColors(
                primary = pri,
                secondary = sec,
                deep = deep,
                cardBackground = Color(0xFF0B0D11),
                cardBorder = Color(0x33E2E8F0),
                progressBarBrush = Brush.horizontalGradient(listOf(deep, pri, sec)),
                cardBorderBrush = Brush.linearGradient(listOf(Color(0x55E2E8F0), Color(0x18FFFFFF), Color(0x22FFFFFF))),
                screenBackground = Color(0xFF060709),
                cardElevated = Color(0xFF181C24),
                trackBackground = Color(0xFF1F242F),
                innerSurface = Color(0xFF12161D),
                accentGlow = Color(0xFFCBD5E1)
            )
        }
    }
}

val LocalLevelThemeColors = androidx.compose.runtime.compositionLocalOf {
    getLevelThemeColors(ColorPalette.OBSIDIAN_CRIMSON)
}
