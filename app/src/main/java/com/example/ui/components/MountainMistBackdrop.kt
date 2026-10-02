package com.example.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Atmospheric Glassmorphism Backdrop with deep obsidian void,
 * radiant crimson/ruby ambient light orbs, and crystalline refraction lines.
 * Optimized with [drawWithCache] to eliminate all per-frame Path/Brush allocations
 * for ultra-smooth 120Hz rendering with zero GC overhead.
 */
@Composable
fun MountainMistBackdrop(
    modifier: Modifier = Modifier,
    alpha: Float = 1.0f
) {
    Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val w = size.width
                val h = size.height

                // 1. Deep Obsidian Void Base
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

                // 2. Radiant Crimson Ambient Light Orb
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

                // 3. Deep Ruby Glow Orb
                val bottomOrbBrush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x4DE50914).copy(alpha = alpha * 0.65f),
                        Color(0x28800A1D).copy(alpha = alpha * 0.45f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.15f, h * 0.82f),
                    radius = w * 0.70f
                )

                // 4. Center Coral Refraction Flare
                val centerFlareBrush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x2BFF526F).copy(alpha = alpha * 0.45f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.45f, h * 0.48f),
                    radius = w * 0.50f
                )

                // 5. Crystalline Glass Geometric Light Horizons
                val ridge1 = Path().apply {
                    moveTo(0f, h * 0.52f)
                    cubicTo(
                        w * 0.25f, h * 0.46f,
                        w * 0.65f, h * 0.58f,
                        w, h * 0.48f
                    )
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                val ridge1Brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x221F101A).copy(alpha = alpha),
                        Color(0x33120810).copy(alpha = alpha)
                    ),
                    startY = h * 0.46f,
                    endY = h * 0.80f
                )

                val ridgeLine1 = Path().apply {
                    moveTo(0f, h * 0.52f)
                    cubicTo(
                        w * 0.25f, h * 0.46f,
                        w * 0.65f, h * 0.58f,
                        w, h * 0.48f
                    )
                }
                val ridgeLine1Brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0x10FFFFFF),
                        Color(0x38FFFFFF),
                        Color(0x4DFF2A4D),
                        Color(0x10FFFFFF)
                    )
                )

                val ridge2 = Path().apply {
                    moveTo(0f, h * 0.68f)
                    cubicTo(
                        w * 0.35f, h * 0.62f,
                        w * 0.70f, h * 0.72f,
                        w, h * 0.64f
                    )
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                val ridge2Brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x33170D18).copy(alpha = alpha),
                        Color(0x440B060D).copy(alpha = alpha)
                    ),
                    startY = h * 0.62f,
                    endY = h
                )

                val ridgeLine2 = Path().apply {
                    moveTo(0f, h * 0.68f)
                    cubicTo(
                        w * 0.35f, h * 0.62f,
                        w * 0.70f, h * 0.72f,
                        w, h * 0.64f
                    )
                }
                val ridgeLine2Brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0x08FFFFFF),
                        Color(0x28FF2A4D),
                        Color(0x40FFFFFF),
                        Color(0x10FF2A4D)
                    )
                )

                val stroke1 = Stroke(width = 1.5f)
                val stroke2 = Stroke(width = 1.2f)

                // 6. Frosted Mist Wash
                val mistBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x12FF2A4D).copy(alpha = alpha * 0.3f),
                        Color(0x26070709).copy(alpha = alpha * 0.7f),
                        Color(0x59070709).copy(alpha = alpha * 0.95f)
                    ),
                    startY = h * 0.35f,
                    endY = h
                )

                onDrawBehind {
                    drawRect(brush = baseBrush)
                    drawCircle(
                        brush = topOrbBrush,
                        radius = w * 0.80f,
                        center = Offset(w * 0.85f, h * 0.20f)
                    )
                    drawCircle(
                        brush = bottomOrbBrush,
                        radius = w * 0.70f,
                        center = Offset(w * 0.15f, h * 0.82f)
                    )
                    drawCircle(
                        brush = centerFlareBrush,
                        radius = w * 0.50f,
                        center = Offset(w * 0.45f, h * 0.48f)
                    )
                    drawPath(path = ridge1, brush = ridge1Brush)
                    drawPath(path = ridgeLine1, brush = ridgeLine1Brush, style = stroke1)
                    drawPath(path = ridge2, brush = ridge2Brush)
                    drawPath(path = ridgeLine2, brush = ridgeLine2Brush, style = stroke2)
                    drawRect(brush = mistBrush)
                }
            }
    )
}
