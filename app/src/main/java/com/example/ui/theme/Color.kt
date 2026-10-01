package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================================
// PROJECT ATLAS - DYNAMIC GLASSMORPHISM THEME PALETTE
// ============================================================================

// CompositionLocals for dynamic launcher theming
val LocalAtlasAccent = compositionLocalOf { Color(0xFFFF2A4D) }
val LocalAtlasBorderBrush = compositionLocalOf {
    Brush.linearGradient(
        colors = listOf(
            Color(0x55FFFFFF),
            Color(0x1AFFFFFF),
            Color(0x55FF2A4D)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}
val LocalAtlasCardBackgroundBrush = compositionLocalOf {
    Brush.linearGradient(
        colors = listOf(
            Color(0x38281A2A),
            Color(0x22130C18)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}

// 1. Black & Obsidian Void Base
val GlassBlackDeep = Color(0xFF070709)          // #070709 - Purest Obsidian Void
val GlassBlackSecondary = Color(0xFF0D0D12)     // #0D0D12 - Space Charcoal
val GlassBlackElevated = Color(0xFF14141B)      // #14141B - Deep Tinted Glass Base
val GlassVoidPitch = Color(0xFF050507)          // #050507 - Absolute Pitch Black

// 2. Glassmorphic Translucent Surfaces (Frosted Glass Effect)
val GlassSurface = Color(0x381E1522)            // Translucent Dark Tint
val GlassSurfaceElevated = Color(0x4D2A1828)    // Translucent Elevated Glass
val GlassSurfaceUltraLight = Color(0x14FFFFFF)  // 8% White Frosted Specular Wash
val GlassSurfacePill = Color(0x28FFFFFF)        // 16% White Translucent Pill
val GlassSurfaceDarkPill = Color(0x55180E1C)    // 33% Deep Translucent Pill

// 3. Glass Borders & Specular Rim Lights
val GlassBorderWhite = Color(0x40FFFFFF)        // 25% Pure White Specular Rim
val GlassBorderSubtle = Color(0x20FFFFFF)       // 12% White Subtle Rim
val GlassBorderCrimson = Color(0x59FF2A4D)      // Edge Glow
val GlassBorderCrimsonSubtle = Color(0x33FF2A4D)// Subtle Rim

// 4. Dynamic Primary & Luxury Theme Accents (Adapt with active theme & wallpaper)
val CrimsonElectric: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current

val CrimsonRuby: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current

val CrimsonCoral: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current

val CrimsonWine: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current

val CrimsonGlow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current.copy(alpha = 0.30f)

val CrimsonGlowSubtle: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current.copy(alpha = 0.15f)

val CrimsonContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current.copy(alpha = 0.20f)

// 5. White & Platinum Silver Typography
val TextPureWhite = Color(0xFFFFFFFF)           // #FFFFFF - 100% Crisp White
val TextFrostedWhite = Color(0xF2FFFFFF)        // #F2FFFFFF - 95% Frosted White
val TextSecondary = Color(0xFFB4B4C4)           // #B4B4C4 - Soft Platinum Silver
val TextMuted = Color(0xFF707084)               // #707084 - Muted Slate Gray
val TextCrimsonMuted = Color(0xFFFFA4B2)        // Soft Tint Silver

// Backward Compatibility Aliases for Atlas Core Components
val AtlasBackground = GlassBlackDeep
val AtlasSecondary = GlassBlackSecondary
val AtlasCard = GlassSurface
val AtlasCardElevated = GlassSurfaceElevated
val AtlasBorder = GlassBorderWhite
val AtlasBorderSubtle = GlassBorderSubtle

val ObsidianBackground = GlassBlackDeep
val CarbonSurface = GlassSurface
val CarbonSurfaceElevated = GlassSurfaceElevated
val CarbonBorder = GlassBorderWhite
val CarbonBorderSubtle = GlassBorderSubtle

val AtlasAmber: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current

val AtlasAmberGlow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current.copy(alpha = 0.30f)

val TextPrimary = TextPureWhite

val SoftSageGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current

val SoftSageGreenGlow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current.copy(alpha = 0.30f)

val SoftSageGreenLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current

val WarmSand = TextPureWhite
val WarmSandDim = TextSecondary
val WarmSandGlow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasAccent.current.copy(alpha = 0.15f)

val SuccessMutedGreen = Color(0xFF00E676)       // Vibrant Emerald Green for completed tasks

// Dynamic Glassmorphism Brushes
val GlassCardBackgroundBrush: Brush
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasCardBackgroundBrush.current

val GlassElevatedBackgroundBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x4D361F34),
        Color(0x2E1B1024)
    ),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

val GlassBorderBrush: Brush
    @Composable
    @ReadOnlyComposable
    get() = LocalAtlasBorderBrush.current

val GlassBorderCrimsonBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x80FF2A4D),
        Color(0x40FFFFFF),
        Color(0x66FF2A4D)
    )
)

val CrimsonExecuteButtonBrush: Brush
    @Composable
    get() {
        val accent = LocalAtlasAccent.current
        return Brush.horizontalGradient(
            colors = listOf(
                accent,
                accent.copy(alpha = 0.85f)
            )
        )
    }
