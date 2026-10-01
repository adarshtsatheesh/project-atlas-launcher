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

@Composable
fun MyApplicationTheme(
    palette: ColorPalette = ColorPalette.OBSIDIAN_CRIMSON,
    content: @Composable () -> Unit
) {
    val accentColor = remember(palette) {
        try {
            Color(android.graphics.Color.parseColor(palette.hexCode))
        } catch (e: Exception) {
            Color(0xFFFF2A4D)
        }
    }

    val cardBrush = remember(palette, accentColor) {
        when (palette) {
            ColorPalette.OBSIDIAN_CRIMSON -> Brush.linearGradient(
                colors = listOf(Color(0x38281A2A), Color(0x22130C18)),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
            ColorPalette.CYBER_EMERALD -> Brush.linearGradient(
                colors = listOf(Color(0x330D2B1E), Color(0x20071A12)),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
            ColorPalette.ELECTRIC_AZURE -> Brush.linearGradient(
                colors = listOf(Color(0x330F2444), Color(0x2008152B)),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
            ColorPalette.AMBER_GOLD -> Brush.linearGradient(
                colors = listOf(Color(0x38291B0C), Color(0x22160F06)),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
            ColorPalette.NEON_VIOLET -> Brush.linearGradient(
                colors = listOf(Color(0x3823103D), Color(0x22140824)),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
            ColorPalette.MINIMAL_MONOCHROME -> Brush.linearGradient(
                colors = listOf(Color(0x2E1C222A), Color(0x1F11151A)),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        }
    }

    val borderBrush = remember(palette, accentColor) {
        Brush.linearGradient(
            colors = listOf(
                Color(0x55FFFFFF),
                Color(0x1AFFFFFF),
                accentColor.copy(alpha = 0.45f)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    val colorScheme = remember(accentColor) {
        darkColorScheme(
            primary = accentColor,
            onPrimary = TextPureWhite,
            primaryContainer = accentColor.copy(alpha = 0.20f),
            onPrimaryContainer = TextPureWhite,
            secondary = TextPureWhite,
            onSecondary = GlassBlackDeep,
            secondaryContainer = GlassSurfaceElevated,
            onSecondaryContainer = TextSecondary,
            tertiary = accentColor,
            onTertiary = GlassBlackDeep,
            background = GlassBlackDeep,
            onBackground = TextPureWhite,
            surface = GlassSurface,
            onSurface = TextPureWhite,
            surfaceVariant = GlassSurfaceElevated,
            onSurfaceVariant = TextSecondary,
            outline = GlassBorderWhite,
            outlineVariant = GlassBorderSubtle
        )
    }

    CompositionLocalProvider(
        LocalAtlasAccent provides accentColor,
        LocalAtlasBorderBrush provides borderBrush,
        LocalAtlasCardBackgroundBrush provides cardBrush
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
