package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.domain.model.BuiltInWallpaper
import java.io.File

/**
 * High-performance, zero-allocation atmospheric backdrop renderer.
 * Supports the signature Obsidian Crimson wallpaper, Aurora Emerald, Azure Horizon,
 * Amber Eclipse, Neon Twilight, Stealth Carbon OLED, and Custom Gallery photos.
 */
@Composable
fun AtlasWallpaperBackdrop(
    wallpaper: BuiltInWallpaper,
    customImagePath: String? = null,
    dimAlpha: Float = 0.35f,
    modifier: Modifier = Modifier
) {
    // Cache the existence check: File.exists() is disk I/O and this condition is
    // re-evaluated on every recomposition of the home screen (dim/slider updates).
    val hasCustomImage = remember(wallpaper, customImagePath) {
        wallpaper == BuiltInWallpaper.CUSTOM_GALLERY &&
            !customImagePath.isNullOrEmpty() &&
            File(customImagePath).exists()
    }
    Box(modifier = modifier.fillMaxSize()) {
        if (hasCustomImage) {
            // Render custom photo from gallery with Coil
            AsyncImage(
                model = File(customImagePath),
                contentDescription = "Custom Wallpaper",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Optimized Glassmorphic Scrim to guarantee clock, widgets & text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimAlpha.coerceIn(0.1f, 0.85f)))
            )
            // Atmospheric subtle top & bottom gradient wash
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val scrim = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x99000000),
                                Color(0x33000000),
                                Color(0xB3000000)
                            )
                        )
                        onDrawBehind {
                            drawRect(brush = scrim)
                        }
                    }
            )
        } else {
            // Built-in procedural wallpapers rendered with drawWithCache (zero GC per frame)
            WallpaperCanvas(wallpaper = wallpaper)
        }
    }
}

/**
 * Lightweight procedural canvas renderer for both full-screen home backdrop
 * and mini cards in settings.
 */
@Composable
fun WallpaperCanvas(
    wallpaper: BuiltInWallpaper,
    modifier: Modifier = Modifier,
    alpha: Float = 1.0f
) {
    Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val w = size.width
                val h = size.height

                when (wallpaper) {
                    BuiltInWallpaper.OBSIDIAN_VOID -> {
                        // 1. Signature Obsidian Horizon (The Original Atlas Aesthetic)
                        val baseBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF070709).copy(alpha = alpha),
                                Color(0xFF0A0A0E).copy(alpha = alpha),
                                Color(0xFF0F0E14).copy(alpha = alpha),
                                Color(0xFF08070B).copy(alpha = alpha)
                            ),
                            startY = 0f,
                            endY = h
                        )
                        val topOrbBrush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x66FF2A4D).copy(alpha = alpha * 0.75f),
                                Color(0x38C61A36).copy(alpha = alpha * 0.65f),
                                Color(0x18600818).copy(alpha = alpha * 0.40f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.85f, h * 0.20f),
                            radius = w * 0.80f
                        )
                        val bottomOrbBrush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x4DE50914).copy(alpha = alpha * 0.65f),
                                Color(0x28800A1D).copy(alpha = alpha * 0.45f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.15f, h * 0.82f),
                            radius = w * 0.70f
                        )
                        val centerFlareBrush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x2BFF526F).copy(alpha = alpha * 0.45f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.45f, h * 0.48f),
                            radius = w * 0.50f
                        )
                        val ridge1 = Path().apply {
                            moveTo(0f, h * 0.52f)
                            cubicTo(w * 0.25f, h * 0.46f, w * 0.65f, h * 0.58f, w, h * 0.48f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        val ridge1Brush = Brush.verticalGradient(
                            colors = listOf(Color(0x221F101A).copy(alpha = alpha), Color(0x33120810).copy(alpha = alpha)),
                            startY = h * 0.46f,
                            endY = h * 0.80f
                        )
                        val ridgeLine1 = Path().apply {
                            moveTo(0f, h * 0.52f)
                            cubicTo(w * 0.25f, h * 0.46f, w * 0.65f, h * 0.58f, w, h * 0.48f)
                        }
                        val ridgeLine1Brush = Brush.horizontalGradient(
                            colors = listOf(Color(0x10FFFFFF), Color(0x38FFFFFF), Color(0x4DFF2A4D), Color(0x10FFFFFF))
                        )
                        val stroke = Stroke(width = 1.5f)

                        onDrawBehind {
                            drawRect(brush = baseBrush)
                            drawCircle(brush = topOrbBrush, radius = w * 0.80f, center = Offset(w * 0.85f, h * 0.20f))
                            drawCircle(brush = bottomOrbBrush, radius = w * 0.70f, center = Offset(w * 0.15f, h * 0.82f))
                            drawCircle(brush = centerFlareBrush, radius = w * 0.50f, center = Offset(w * 0.45f, h * 0.48f))
                            drawPath(path = ridge1, brush = ridge1Brush)
                            drawPath(path = ridgeLine1, brush = ridgeLine1Brush, style = stroke)
                        }
                    }

                    BuiltInWallpaper.CYBER_EMERALD -> {
                        // 2. Cyber Emerald Aurora
                        val baseBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF040A07).copy(alpha = alpha),
                                Color(0xFF06120D).copy(alpha = alpha),
                                Color(0xFF051710).copy(alpha = alpha),
                                Color(0xFF030806).copy(alpha = alpha)
                            )
                        )
                        val emeraldOrb = Brush.radialGradient(
                            colors = listOf(
                                Color(0x5510B981).copy(alpha = alpha * 0.8f),
                                Color(0x28059669).copy(alpha = alpha * 0.5f),
                                Color(0x10047857).copy(alpha = alpha * 0.3f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.20f, h * 0.25f),
                            radius = w * 0.85f
                        )
                        val tealGlow = Brush.radialGradient(
                            colors = listOf(
                                Color(0x4014B8A6).copy(alpha = alpha * 0.7f),
                                Color(0x150F766E).copy(alpha = alpha * 0.4f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.80f, h * 0.75f),
                            radius = w * 0.75f
                        )
                        val wavePath = Path().apply {
                            moveTo(0f, h * 0.48f)
                            cubicTo(w * 0.3f, h * 0.40f, w * 0.7f, h * 0.55f, w, h * 0.45f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        val waveBrush = Brush.verticalGradient(
                            colors = listOf(Color(0x200D2B1E), Color(0x35071A12))
                        )
                        val waveLine = Path().apply {
                            moveTo(0f, h * 0.48f)
                            cubicTo(w * 0.3f, h * 0.40f, w * 0.7f, h * 0.55f, w, h * 0.45f)
                        }
                        val waveLineBrush = Brush.horizontalGradient(
                            colors = listOf(Color(0x15FFFFFF), Color(0x5010B981), Color(0x3534D399), Color(0x10FFFFFF))
                        )
                        val stroke = Stroke(width = 1.5f)

                        onDrawBehind {
                            drawRect(brush = baseBrush)
                            drawCircle(brush = emeraldOrb, radius = w * 0.85f, center = Offset(w * 0.20f, h * 0.25f))
                            drawCircle(brush = tealGlow, radius = w * 0.75f, center = Offset(w * 0.80f, h * 0.75f))
                            drawPath(path = wavePath, brush = waveBrush)
                            drawPath(path = waveLine, brush = waveLineBrush, style = stroke)
                        }
                    }

                    BuiltInWallpaper.ELECTRIC_AZURE -> {
                        // 3. Electric Azure Horizon
                        val baseBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF030712).copy(alpha = alpha),
                                Color(0xFF081026).copy(alpha = alpha),
                                Color(0xFF0A1836).copy(alpha = alpha),
                                Color(0xFF020617).copy(alpha = alpha)
                            )
                        )
                        val cyanOrb = Brush.radialGradient(
                            colors = listOf(
                                Color(0x550284C7).copy(alpha = alpha * 0.8f),
                                Color(0x250369A1).copy(alpha = alpha * 0.5f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.75f, h * 0.30f),
                            radius = w * 0.80f
                        )
                        val cobaltOrb = Brush.radialGradient(
                            colors = listOf(
                                Color(0x4038BDF8).copy(alpha = alpha * 0.65f),
                                Color(0x150284C7).copy(alpha = alpha * 0.35f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.25f, h * 0.70f),
                            radius = w * 0.70f
                        )
                        val ridge = Path().apply {
                            moveTo(0f, h * 0.54f)
                            cubicTo(w * 0.35f, h * 0.60f, w * 0.65f, h * 0.48f, w, h * 0.52f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        val ridgeBrush = Brush.verticalGradient(
                            colors = listOf(Color(0x250F2444), Color(0x3508152B))
                        )
                        val ridgeLine = Path().apply {
                            moveTo(0f, h * 0.54f)
                            cubicTo(w * 0.35f, h * 0.60f, w * 0.65f, h * 0.48f, w, h * 0.52f)
                        }
                        val ridgeLineBrush = Brush.horizontalGradient(
                            colors = listOf(Color(0x10FFFFFF), Color(0x500284C7), Color(0x4038BDF8), Color(0x10FFFFFF))
                        )
                        val stroke = Stroke(width = 1.5f)

                        onDrawBehind {
                            drawRect(brush = baseBrush)
                            drawCircle(brush = cyanOrb, radius = w * 0.80f, center = Offset(w * 0.75f, h * 0.30f))
                            drawCircle(brush = cobaltOrb, radius = w * 0.70f, center = Offset(w * 0.25f, h * 0.70f))
                            drawPath(path = ridge, brush = ridgeBrush)
                            drawPath(path = ridgeLine, brush = ridgeLineBrush, style = stroke)
                        }
                    }

                    BuiltInWallpaper.AMBER_ECLIPSE -> {
                        // 4. Amber Eclipse
                        val baseBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0C0A05).copy(alpha = alpha),
                                Color(0xFF140F08).copy(alpha = alpha),
                                Color(0xFF1C1309).copy(alpha = alpha),
                                Color(0xFF090703).copy(alpha = alpha)
                            )
                        )
                        val goldCorona = Brush.radialGradient(
                            colors = listOf(
                                Color(0x60F59E0B).copy(alpha = alpha * 0.8f),
                                Color(0x30D97706).copy(alpha = alpha * 0.5f),
                                Color(0x15B45309).copy(alpha = alpha * 0.3f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.50f, h * 0.28f),
                            radius = w * 0.75f
                        )
                        val warmFlame = Brush.radialGradient(
                            colors = listOf(
                                Color(0x40EF4444).copy(alpha = alpha * 0.6f),
                                Color(0x15F59E0B).copy(alpha = alpha * 0.3f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.15f, h * 0.75f),
                            radius = w * 0.65f
                        )
                        val horizon = Path().apply {
                            moveTo(0f, h * 0.50f)
                            cubicTo(w * 0.35f, h * 0.44f, w * 0.65f, h * 0.54f, w, h * 0.47f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        val horizonBrush = Brush.verticalGradient(
                            colors = listOf(Color(0x28291B0C), Color(0x40160F06))
                        )
                        val horizonLine = Path().apply {
                            moveTo(0f, h * 0.50f)
                            cubicTo(w * 0.35f, h * 0.44f, w * 0.65f, h * 0.54f, w, h * 0.47f)
                        }
                        val horizonLineBrush = Brush.horizontalGradient(
                            colors = listOf(Color(0x15FFFFFF), Color(0x55F59E0B), Color(0x40FBBF24), Color(0x10FFFFFF))
                        )
                        val stroke = Stroke(width = 1.5f)

                        onDrawBehind {
                            drawRect(brush = baseBrush)
                            drawCircle(brush = goldCorona, radius = w * 0.75f, center = Offset(w * 0.50f, h * 0.28f))
                            drawCircle(brush = warmFlame, radius = w * 0.65f, center = Offset(w * 0.15f, h * 0.75f))
                            drawPath(path = horizon, brush = horizonBrush)
                            drawPath(path = horizonLine, brush = horizonLineBrush, style = stroke)
                        }
                    }

                    BuiltInWallpaper.VIOLET_TWILIGHT -> {
                        // 5. Neon Violet Twilight
                        val baseBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF090511).copy(alpha = alpha),
                                Color(0xFF110822).copy(alpha = alpha),
                                Color(0xFF190C31).copy(alpha = alpha),
                                Color(0xFF07030D).copy(alpha = alpha)
                            )
                        )
                        val violetOrb = Brush.radialGradient(
                            colors = listOf(
                                Color(0x55A855F7).copy(alpha = alpha * 0.8f),
                                Color(0x257E22CE).copy(alpha = alpha * 0.5f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.75f, h * 0.22f),
                            radius = w * 0.85f
                        )
                        val magentaOrb = Brush.radialGradient(
                            colors = listOf(
                                Color(0x45EC4899).copy(alpha = alpha * 0.7f),
                                Color(0x15BE185D).copy(alpha = alpha * 0.4f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.20f, h * 0.80f),
                            radius = w * 0.70f
                        )
                        val ridge = Path().apply {
                            moveTo(0f, h * 0.55f)
                            cubicTo(w * 0.3f, h * 0.50f, w * 0.7f, h * 0.62f, w, h * 0.54f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        val ridgeBrush = Brush.verticalGradient(
                            colors = listOf(Color(0x2523103D), Color(0x35140824))
                        )
                        val ridgeLine = Path().apply {
                            moveTo(0f, h * 0.55f)
                            cubicTo(w * 0.3f, h * 0.50f, w * 0.7f, h * 0.62f, w, h * 0.54f)
                        }
                        val ridgeLineBrush = Brush.horizontalGradient(
                            colors = listOf(Color(0x10FFFFFF), Color(0x50A855F7), Color(0x40EC4899), Color(0x10FFFFFF))
                        )
                        val stroke = Stroke(width = 1.5f)

                        onDrawBehind {
                            drawRect(brush = baseBrush)
                            drawCircle(brush = violetOrb, radius = w * 0.85f, center = Offset(w * 0.75f, h * 0.22f))
                            drawCircle(brush = magentaOrb, radius = w * 0.70f, center = Offset(w * 0.20f, h * 0.80f))
                            drawPath(path = ridge, brush = ridgeBrush)
                            drawPath(path = ridgeLine, brush = ridgeLineBrush, style = stroke)
                        }
                    }

                    BuiltInWallpaper.STEALTH_OLED -> {
                        // 6. Stealth Carbon OLED (Pure black 0x000000 + minimal titanium micro-crosshair)
                        val pureBlack = Color(0xFF000000)
                        val subtleTitanium = Color(0x12FFFFFF)
                        val stroke = Stroke(width = 1f)

                        onDrawBehind {
                            drawRect(color = pureBlack)
                            // Clean minimal architectural horizon line
                            drawLine(
                                color = subtleTitanium,
                                start = Offset(0f, h * 0.50f),
                                end = Offset(w, h * 0.50f),
                                strokeWidth = 1f
                            )
                            // Subtle center crosshair
                            drawLine(
                                color = Color(0x20FFFFFF),
                                start = Offset(w * 0.50f - 12f, h * 0.50f),
                                end = Offset(w * 0.50f + 12f, h * 0.50f),
                                strokeWidth = 1.5f
                            )
                            drawLine(
                                color = Color(0x20FFFFFF),
                                start = Offset(w * 0.50f, h * 0.50f - 12f),
                                end = Offset(w * 0.50f, h * 0.50f + 12f),
                                strokeWidth = 1.5f
                            )
                        }
                    }

                    BuiltInWallpaper.CUSTOM_GALLERY -> {
                        // Fallback when custom photo isn't yet selected
                        val fallbackBrush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0A0A0F), Color(0xFF14141E), Color(0xFF08080C))
                        )
                        onDrawBehind {
                            drawRect(brush = fallbackBrush)
                        }
                    }
                }
            }
    )
}
