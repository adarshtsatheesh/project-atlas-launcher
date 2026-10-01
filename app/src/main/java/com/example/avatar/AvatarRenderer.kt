package com.example.avatar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

private val OutlineColor = Color(0x38101522)
private val ShadowOverlay = Color(0x28000000)

/**
 * Premium stylized vector-like digital cartoon avatar renderer.
 *
 * Inspired by modern digital avatars (Bitmoji, Memoji, Ready Player Me, ZEPETO):
 * - Large stylized head with rounded features & friendly confident expression
 * - Full body visible from head to toe
 * - Clean outlines, soft drop shadows, and smooth gradients
 * - Occasional natural blinking and subtle idle breathing & floating animation
 * - Modular clothing, hairstyles, facial hair, glasses, hats, and watches
 */
@Composable
fun AvatarRenderer(
    config: AvatarConfig,
    level: Int = 1,
    modifier: Modifier = Modifier,
    enableAnimation: Boolean = true
) {
    // 1. Idle breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")
    val breathPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "breath_phase"
    )

    // 2. Tiny floating hover animation
    val floatY by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )

    // 3. Natural periodic blinking animation (blinks every ~4.2 seconds)
    val blinkAnim = remember { Animatable(0f) }
    if (enableAnimation) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3800 + (Math.random() * 1200).toLong())
                // Quick natural blink down and up
                blinkAnim.animateTo(1f, tween(110, easing = FastOutSlowInEasing))
                blinkAnim.animateTo(0f, tween(110, easing = FastOutSlowInEasing))
            }
        }
    }

    val chestLift = if (enableAnimation) (sin(breathPhase.toDouble()) * 2.2f).toFloat() else 0f
    val currentFloatY = if (enableAnimation) floatY else 0f
    val blinkFactor = blinkAnim.value

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val totalH = h * 0.88f
            val baseY = h * 0.94f + currentFloatY
            val cx = w * 0.5f

            // 1. Floor Drop Shadow
            drawFloorShadow(cx, baseY, w)

            // Proportions: Stylized digital avatar (large head ~20% of total height)
            val headH = totalH * 0.215f
            val headW = headH * 0.82f

            val headTopY = baseY - totalH
            val headCenterY = headTopY + headH * 0.48f
            val chinY = headTopY + headH
            val neckY = chinY + 12f

            val shoulderY = neckY + 16f - chestLift
            val chestY = shoulderY + totalH * 0.14f
            val waistY = chestY + totalH * 0.13f
            val hipsY = waistY + totalH * 0.08f
            val kneesY = hipsY + totalH * 0.20f
            val anklesY = kneesY + totalH * 0.17f
            val solesY = baseY - 2f

            val legSpread = w * 0.085f
            val leftLegX = cx - legSpread
            val rightLegX = cx + legSpread

            // 2. Footwear & Shoes
            drawShoes(
                leftX = leftLegX,
                rightX = rightLegX,
                ankleY = anklesY,
                soleY = solesY,
                footwear = config.footwear,
                footwearColor = Color(config.footwearColor)
            )

            // 3. Pants / Lower Body
            drawPants(
                cx = cx,
                hipsY = hipsY,
                kneesY = kneesY,
                anklesY = anklesY,
                leftLegX = leftLegX,
                rightLegX = rightLegX,
                pantsType = config.pantsType,
                pantsColor = Color(config.pantsColor),
                skinTone = config.skinTone
            )

            // 4. Upper Body / Outfit (Hoodie, Bomber, Jacket, etc.)
            val shoulderW = totalH * 0.27f
            val leftShoulderX = cx - shoulderW * 0.5f
            val rightShoulderX = cx + shoulderW * 0.5f

            drawUpperBody(
                cx = cx,
                neckY = neckY,
                shoulderY = shoulderY,
                chestY = chestY,
                waistY = waistY,
                hipsY = hipsY,
                leftShoulderX = leftShoulderX,
                rightShoulderX = rightShoulderX,
                outfit = config.outfitType,
                primaryColor = Color(config.outfitPrimaryColor),
                secondaryColor = Color(config.outfitSecondaryColor),
                skinTone = config.skinTone
            )

            // 5. Arms & Hands
            drawArms(
                cx = cx,
                leftShoulderX = leftShoulderX,
                rightShoulderX = rightShoulderX,
                shoulderY = shoulderY,
                chestY = chestY,
                waistY = waistY,
                hipsY = hipsY,
                outfit = config.outfitType,
                primaryColor = Color(config.outfitPrimaryColor),
                skinTone = config.skinTone,
                watch = config.watch
            )

            // 6. Neck & Head Base
            drawNeckAndFace(
                cx = cx,
                headCenterY = headCenterY,
                headTopY = headTopY,
                chinY = chinY,
                neckY = neckY,
                headW = headW,
                headH = headH,
                skinTone = config.skinTone,
                faceShape = config.faceShape
            )

            // 7. Facial Features (Eyes with blink, Eyebrows, Nose, Mouth, Beard)
            drawFacialFeatures(
                cx = cx,
                headCenterY = headCenterY,
                headW = headW,
                headH = headH,
                eyeShape = config.eyeShape,
                eyeColor = config.eyeColor,
                eyebrowStyle = config.eyebrowStyle,
                beardStyle = config.beardStyle,
                beardColor = config.beardColor,
                skinTone = config.skinTone,
                hairColor = config.hairColor,
                blinkFactor = blinkFactor
            )

            // 8. Glasses (if equipped)
            if (config.glasses != GlassesType.NONE) {
                drawGlasses(
                    cx = cx,
                    headCenterY = headCenterY,
                    headW = headW,
                    glasses = config.glasses
                )
            }

            // 9. Hair & Front Bangs
            drawHair(
                cx = cx,
                headTopY = headTopY,
                headCenterY = headCenterY,
                headW = headW,
                headH = headH,
                hairStyle = config.hairStyle,
                hairColor = config.hairColor
            )

            // 10. Hat / Cap (if equipped)
            if (config.hat != HatType.NONE) {
                drawHat(
                    cx = cx,
                    headTopY = headTopY,
                    headW = headW,
                    hat = config.hat
                )
            }
        }
    }
}

/**
 * Draws soft drop shadow disc on the floor.
 */
private fun DrawScope.drawFloorShadow(cx: Float, baseY: Float, w: Float) {
    val shadowW = w * 0.46f
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x55000000), Color(0x22000000), Color.Transparent),
            center = Offset(cx, baseY),
            radius = shadowW * 0.5f
        ),
        topLeft = Offset(cx - shadowW * 0.5f, baseY - 8f),
        size = Size(shadowW, 16f)
    )
}

/**
 * Draws modern sneakers or boots.
 */
private fun DrawScope.drawShoes(
    leftX: Float,
    rightX: Float,
    ankleY: Float,
    soleY: Float,
    footwear: FootwearType,
    footwearColor: Color
) {
    val shoeW = 28f
    val shoeH = soleY - ankleY
    val soleH = 6f

    val positions = listOf(Pair(leftX, -1f), Pair(rightX, 1f))

    for ((x, dir) in positions) {
        val toeX = x + (14f * dir)
        val heelX = x - (11f * dir)

        // 1. Rubber sole
        val solePath = Path().apply {
            moveTo(heelX, soleY - soleH)
            lineTo(toeX, soleY - soleH)
            quadraticTo(toeX + 3f * dir, soleY - soleH * 0.5f, toeX, soleY)
            lineTo(heelX, soleY)
            quadraticTo(heelX - 2f * dir, soleY - soleH * 0.5f, heelX, soleY - soleH)
            close()
        }
        drawPath(path = solePath, color = Color(0xFFF1F5F9))
        drawPath(path = solePath, color = OutlineColor, style = Stroke(width = 1.2f))

        // 2. Shoe upper
        val upperPath = Path().apply {
            moveTo(x - 7f, ankleY)
            lineTo(x + 7f, ankleY)
            lineTo(toeX, soleY - soleH)
            lineTo(heelX, soleY - soleH)
            close()
        }
        drawPath(path = upperPath, color = footwearColor)
        drawPath(path = upperPath, color = OutlineColor, style = Stroke(width = 1.2f))

        // Toe cap detail
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = 3.5f,
            center = Offset(toeX - 3f * dir, soleY - soleH - 2f)
        )
    }
}

/**
 * Draws stylized pants / cargo joggers.
 */
private fun DrawScope.drawPants(
    cx: Float,
    hipsY: Float,
    kneesY: Float,
    anklesY: Float,
    leftLegX: Float,
    rightLegX: Float,
    pantsType: PantsType,
    pantsColor: Color,
    skinTone: SkinTone
) {
    val waistHalfW = 24f
    val crotchY = hipsY + (kneesY - hipsY) * 0.32f

    // Left leg
    val leftPath = Path().apply {
        moveTo(cx - waistHalfW, hipsY)
        lineTo(cx - 2f, crotchY)
        lineTo(leftLegX + 6f, anklesY)
        lineTo(leftLegX - 6f, anklesY)
        quadraticTo(cx - waistHalfW - 5f, kneesY, cx - waistHalfW, hipsY)
        close()
    }
    drawPath(path = leftPath, color = pantsColor)
    drawPath(path = leftPath, color = OutlineColor, style = Stroke(width = 1.4f))

    // Right leg
    val rightPath = Path().apply {
        moveTo(cx + waistHalfW, hipsY)
        lineTo(cx + 2f, crotchY)
        lineTo(rightLegX - 6f, anklesY)
        lineTo(rightLegX + 6f, anklesY)
        quadraticTo(cx + waistHalfW + 5f, kneesY, cx + waistHalfW, hipsY)
        close()
    }
    drawPath(path = rightPath, color = pantsColor)
    drawPath(path = rightPath, color = OutlineColor, style = Stroke(width = 1.4f))

    // Subtle knee seam
    drawLine(
        color = Color(0x33FFFFFF),
        start = Offset(leftLegX - 4f, kneesY),
        end = Offset(leftLegX + 4f, kneesY),
        strokeWidth = 1.2f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0x33FFFFFF),
        start = Offset(rightLegX - 4f, kneesY),
        end = Offset(rightLegX + 4f, kneesY),
        strokeWidth = 1.2f,
        cap = StrokeCap.Round
    )
}

/**
 * Draws modern streetwear upper body (Hoodie, Bomber, etc.) with clean rounded styling.
 */
private fun DrawScope.drawUpperBody(
    cx: Float,
    neckY: Float,
    shoulderY: Float,
    chestY: Float,
    waistY: Float,
    hipsY: Float,
    leftShoulderX: Float,
    rightShoulderX: Float,
    outfit: OutfitType,
    primaryColor: Color,
    secondaryColor: Color,
    skinTone: SkinTone
) {
    // Torso silhouette
    val torsoPath = Path().apply {
        moveTo(leftShoulderX, shoulderY)
        lineTo(rightShoulderX, shoulderY)
        lineTo(cx + 24f, waistY)
        lineTo(cx + 23f, hipsY)
        lineTo(cx - 23f, hipsY)
        lineTo(cx - 24f, waistY)
        close()
    }
    drawPath(path = torsoPath, color = primaryColor)
    drawPath(path = torsoPath, color = OutlineColor, style = Stroke(width = 1.4f))

    when (outfit) {
        OutfitType.MINIMAL_HOODIE -> {
            // Hood folded comfortably around neck
            val hoodCollar = Path().apply {
                moveTo(cx - 18f, neckY - 2f)
                quadraticTo(cx, neckY + 14f, cx + 18f, neckY - 2f)
                quadraticTo(cx + 22f, neckY - 8f, cx + 14f, neckY - 10f)
                quadraticTo(cx, neckY - 4f, cx - 14f, neckY - 10f)
                close()
            }
            drawPath(path = hoodCollar, color = primaryColor.copy(alpha = 0.88f))
            drawPath(path = hoodCollar, color = OutlineColor, style = Stroke(width = 1.2f))

            // White drawstring cords
            drawLine(
                color = Color.White.copy(alpha = 0.9f),
                start = Offset(cx - 6f, neckY + 8f),
                end = Offset(cx - 6f, chestY - 2f),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.9f),
                start = Offset(cx + 6f, neckY + 8f),
                end = Offset(cx + 6f, chestY - 2f),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )

            // Kangaroo pocket
            val pocketY = waistY - 6f
            val pocketW = 32f
            val pocketH = 18f
            drawRoundRect(
                color = ShadowOverlay,
                topLeft = Offset(cx - pocketW * 0.5f, pocketY),
                size = Size(pocketW, pocketH),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(width = 1.2f)
            )
        }
        OutfitType.TECH_BOMBER -> {
            // Center zipper seam
            drawLine(
                color = secondaryColor,
                start = Offset(cx, neckY + 6f),
                end = Offset(cx, hipsY),
                strokeWidth = 1.8f
            )
            // Bomber ribbed collar
            drawArc(
                color = OutlineColor,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - 14f, neckY - 2f),
                size = Size(28f, 14f),
                style = Stroke(width = 3.5f)
            )
        }
        else -> {
            // Clean crewneck collar
            drawArc(
                color = OutlineColor,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - 12f, neckY - 2f),
                size = Size(24f, 12f),
                style = Stroke(width = 2.5f)
            )
        }
    }
}

/**
 * Draws arms and hands with watch accessory.
 */
private fun DrawScope.drawArms(
    cx: Float,
    leftShoulderX: Float,
    rightShoulderX: Float,
    shoulderY: Float,
    chestY: Float,
    waistY: Float,
    hipsY: Float,
    outfit: OutfitType,
    primaryColor: Color,
    skinTone: SkinTone,
    watch: WatchType
) {
    val elbowY = chestY + (waistY - chestY) * 0.6f
    val wristY = hipsY + 8f

    // Left arm
    val leftArm = Path().apply {
        moveTo(leftShoulderX, shoulderY)
        lineTo(leftShoulderX - 7f, elbowY)
        lineTo(leftShoulderX - 6f, wristY)
        lineTo(leftShoulderX + 6f, wristY)
        lineTo(leftShoulderX + 5f, elbowY)
        lineTo(leftShoulderX + 12f, shoulderY + 8f)
        close()
    }
    drawPath(path = leftArm, color = primaryColor)
    drawPath(path = leftArm, color = OutlineColor, style = Stroke(width = 1.4f))

    // Left hand
    drawRoundRect(
        color = skinTone.baseColor,
        topLeft = Offset(leftShoulderX - 5f, wristY),
        size = Size(10f, 14f),
        cornerRadius = CornerRadius(5f, 5f)
    )
    drawRoundRect(
        color = OutlineColor,
        topLeft = Offset(leftShoulderX - 5f, wristY),
        size = Size(10f, 14f),
        cornerRadius = CornerRadius(5f, 5f),
        style = Stroke(width = 1.2f)
    )

    // Right arm
    val rightArm = Path().apply {
        moveTo(rightShoulderX, shoulderY)
        lineTo(rightShoulderX + 7f, elbowY)
        lineTo(rightShoulderX + 6f, wristY)
        lineTo(rightShoulderX - 6f, wristY)
        lineTo(rightShoulderX - 5f, elbowY)
        lineTo(rightShoulderX - 12f, shoulderY + 8f)
        close()
    }
    drawPath(path = rightArm, color = primaryColor)
    drawPath(path = rightArm, color = OutlineColor, style = Stroke(width = 1.4f))

    // Right hand
    drawRoundRect(
        color = skinTone.baseColor,
        topLeft = Offset(rightShoulderX - 5f, wristY),
        size = Size(10f, 14f),
        cornerRadius = CornerRadius(5f, 5f)
    )
    drawRoundRect(
        color = OutlineColor,
        topLeft = Offset(rightShoulderX - 5f, wristY),
        size = Size(10f, 14f),
        cornerRadius = CornerRadius(5f, 5f),
        style = Stroke(width = 1.2f)
    )

    // Watch accessory on left wrist
    if (watch != WatchType.NONE) {
        val watchColor = when (watch) {
            WatchType.GOLD_EXECUTIVE -> Color(0xFFD4AF37)
            WatchType.CHRONO_TITANIUM -> Color(0xFF94A3B8)
            else -> Color(0xFF0F172A)
        }
        drawRoundRect(
            color = watchColor,
            topLeft = Offset(leftShoulderX - 6f, wristY - 3f),
            size = Size(12f, 5f),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }
}

/**
 * Draws rounded stylized head, ears, and neck.
 */
private fun DrawScope.drawNeckAndFace(
    cx: Float,
    headCenterY: Float,
    headTopY: Float,
    chinY: Float,
    neckY: Float,
    headW: Float,
    headH: Float,
    skinTone: SkinTone,
    faceShape: FaceShape
) {
    // 1. Neck with soft shadow beneath chin
    val neckW = headW * 0.42f
    drawRect(
        color = skinTone.shadowColor,
        topLeft = Offset(cx - neckW * 0.5f, headCenterY + headH * 0.28f),
        size = Size(neckW, neckY - (headCenterY + headH * 0.28f) + 4f)
    )
    drawRect(
        color = OutlineColor,
        topLeft = Offset(cx - neckW * 0.5f, headCenterY + headH * 0.28f),
        size = Size(neckW, neckY - (headCenterY + headH * 0.28f) + 4f),
        style = Stroke(width = 1.2f)
    )

    // 2. Ears
    val earY = headCenterY
    val earW = 7f
    val earH = 14f
    drawRoundRect(
        color = skinTone.baseColor,
        topLeft = Offset(cx - headW * 0.52f - earW, earY - earH * 0.5f),
        size = Size(earW, earH),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = skinTone.baseColor,
        topLeft = Offset(cx + headW * 0.52f, earY - earH * 0.5f),
        size = Size(earW, earH),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // 3. Head Shape (Stylized rounded contour)
    val headPath = Path().apply {
        moveTo(cx - headW * 0.48f, headCenterY - headH * 0.22f)
        quadraticTo(cx, headTopY, cx + headW * 0.48f, headCenterY - headH * 0.22f)
        // Cheekbone curve
        quadraticTo(cx + headW * 0.52f, headCenterY + headH * 0.12f, cx + headW * 0.28f, chinY)
        // Chin curve
        quadraticTo(cx, chinY + 5f, cx - headW * 0.28f, chinY)
        quadraticTo(cx - headW * 0.52f, headCenterY + headH * 0.12f, cx - headW * 0.48f, headCenterY - headH * 0.22f)
        close()
    }

    // Soft gradient fill
    drawPath(
        path = headPath,
        brush = Brush.verticalGradient(
            colors = listOf(skinTone.highlightColor, skinTone.baseColor, skinTone.shadowColor),
            startY = headTopY,
            endY = chinY
        )
    )
    // Clean vector outline
    drawPath(path = headPath, color = OutlineColor, style = Stroke(width = 1.4f))
}

/**
 * Draws expressive stylized eyes, eyebrows, nose, mouth, and beard.
 */
private fun DrawScope.drawFacialFeatures(
    cx: Float,
    headCenterY: Float,
    headW: Float,
    headH: Float,
    eyeShape: EyeShape,
    eyeColor: EyeColor,
    eyebrowStyle: EyebrowStyle,
    beardStyle: BeardStyle,
    beardColor: HairColor,
    skinTone: SkinTone,
    hairColor: HairColor,
    blinkFactor: Float
) {
    val eyeY = headCenterY - 2f
    val eyeSpacing = headW * 0.24f

    // 1. Eyebrows
    val browY = eyeY - 8f
    val browW = 10f
    val browColor = hairColor.baseColor

    drawLine(
        color = browColor,
        start = Offset(cx - eyeSpacing - browW * 0.5f, browY),
        end = Offset(cx - eyeSpacing + browW * 0.5f, browY - 1f),
        strokeWidth = 2.4f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = browColor,
        start = Offset(cx + eyeSpacing - browW * 0.5f, browY - 1f),
        end = Offset(cx + eyeSpacing + browW * 0.5f, browY),
        strokeWidth = 2.4f,
        cap = StrokeCap.Round
    )

    // 2. Eyes with blinking physics
    val eyeW = 13f
    val baseEyeH = 8.5f
    val eyeH = baseEyeH * (1f - blinkFactor).coerceIn(0.08f, 1f)

    if (blinkFactor > 0.85f) {
        // Eyelid line when closed
        drawLine(
            color = OutlineColor,
            start = Offset(cx - eyeSpacing - eyeW * 0.5f, eyeY),
            end = Offset(cx - eyeSpacing + eyeW * 0.5f, eyeY),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = OutlineColor,
            start = Offset(cx + eyeSpacing - eyeW * 0.5f, eyeY),
            end = Offset(cx + eyeSpacing + eyeW * 0.5f, eyeY),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
    } else {
        // Left Eye (white sclera, colored iris, reflection)
        drawOval(
            color = Color.White,
            topLeft = Offset(cx - eyeSpacing - eyeW * 0.5f, eyeY - eyeH * 0.5f),
            size = Size(eyeW, eyeH)
        )
        drawOval(
            color = OutlineColor,
            topLeft = Offset(cx - eyeSpacing - eyeW * 0.5f, eyeY - eyeH * 0.5f),
            size = Size(eyeW, eyeH),
            style = Stroke(width = 1.2f)
        )
        drawCircle(
            color = eyeColor.irisColor,
            radius = (eyeH * 0.42f).coerceAtLeast(1.5f),
            center = Offset(cx - eyeSpacing, eyeY)
        )
        drawCircle(
            color = Color(0xFF0F172A),
            radius = (eyeH * 0.22f).coerceAtLeast(1f),
            center = Offset(cx - eyeSpacing, eyeY)
        )
        // White light reflection dot
        drawCircle(
            color = Color.White,
            radius = 1.2f,
            center = Offset(cx - eyeSpacing - 1.2f, eyeY - eyeH * 0.2f)
        )

        // Right Eye
        drawOval(
            color = Color.White,
            topLeft = Offset(cx + eyeSpacing - eyeW * 0.5f, eyeY - eyeH * 0.5f),
            size = Size(eyeW, eyeH)
        )
        drawOval(
            color = OutlineColor,
            topLeft = Offset(cx + eyeSpacing - eyeW * 0.5f, eyeY - eyeH * 0.5f),
            size = Size(eyeW, eyeH),
            style = Stroke(width = 1.2f)
        )
        drawCircle(
            color = eyeColor.irisColor,
            radius = (eyeH * 0.42f).coerceAtLeast(1.5f),
            center = Offset(cx + eyeSpacing, eyeY)
        )
        drawCircle(
            color = Color(0xFF0F172A),
            radius = (eyeH * 0.22f).coerceAtLeast(1f),
            center = Offset(cx + eyeSpacing, eyeY)
        )
        // White light reflection dot
        drawCircle(
            color = Color.White,
            radius = 1.2f,
            center = Offset(cx + eyeSpacing - 1.2f, eyeY - eyeH * 0.2f)
        )
    }

    // 3. Cute rounded button nose
    val noseY = eyeY + 11f
    drawArc(
        color = skinTone.shadowColor,
        startAngle = 10f,
        sweepAngle = 160f,
        useCenter = false,
        topLeft = Offset(cx - 3.5f, noseY - 2f),
        size = Size(7f, 4.5f),
        style = Stroke(width = 1.5f, cap = StrokeCap.Round)
    )

    // 4. Confident & friendly mouth (slight gentle smile)
    val mouthY = noseY + 9f
    drawArc(
        color = Color(0xFF8B4538),
        startAngle = 15f,
        sweepAngle = 150f,
        useCenter = false,
        topLeft = Offset(cx - 7f, mouthY - 3f),
        size = Size(14f, 6.5f),
        style = Stroke(width = 1.8f, cap = StrokeCap.Round)
    )

    // 5. Beard / Facial Hair
    if (beardStyle != BeardStyle.CLEAN_SHAVEN) {
        val bColor = beardColor.baseColor
        when (beardStyle) {
            BeardStyle.LIGHT_STUBBLE -> {
                // Subtle stippled chin and jaw contour
                drawArc(
                    color = bColor.copy(alpha = 0.35f),
                    startAngle = 30f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(cx - headW * 0.32f, mouthY),
                    size = Size(headW * 0.64f, headH * 0.24f),
                    style = Stroke(width = 5f)
                )
            }
            BeardStyle.SHORT_BOXED -> {
                // Clean sculpted boxed beard
                drawArc(
                    color = bColor.copy(alpha = 0.85f),
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(cx - headW * 0.36f, mouthY - 2f),
                    size = Size(headW * 0.72f, headH * 0.28f),
                    style = Stroke(width = 6.5f, cap = StrokeCap.Round)
                )
            }
            BeardStyle.FULL_BEARD -> {
                // Full shaped beard around chin and jaw
                val beardPath = Path().apply {
                    moveTo(cx - headW * 0.38f, mouthY - 4f)
                    quadraticTo(cx - headW * 0.32f, mouthY + headH * 0.26f, cx, mouthY + headH * 0.28f)
                    quadraticTo(cx + headW * 0.32f, mouthY + headH * 0.26f, cx + headW * 0.38f, mouthY - 4f)
                    close()
                }
                drawPath(path = beardPath, color = bColor)
                drawPath(path = beardPath, color = OutlineColor, style = Stroke(width = 1.2f))
            }
            BeardStyle.CLEAN_GOATEE -> {
                drawArc(
                    color = bColor,
                    startAngle = 40f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(cx - 10f, mouthY + 1f),
                    size = Size(20f, 15f),
                    style = Stroke(width = 4.5f, cap = StrokeCap.Round)
                )
            }
            BeardStyle.MODERN_MUSTACHE -> {
                drawLine(
                    color = bColor,
                    start = Offset(cx - 8f, mouthY - 3f),
                    end = Offset(cx + 8f, mouthY - 3f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
            else -> {}
        }
    }
}

/**
 * Draws stylish modern glasses.
 */
private fun DrawScope.drawGlasses(
    cx: Float,
    headCenterY: Float,
    headW: Float,
    glasses: GlassesType
) {
    val eyeY = headCenterY - 2f
    val spacing = headW * 0.24f
    val frameColor = when (glasses) {
        GlassesType.ROUND_WIRE -> Color(0xFFD4AF37) // Gold wire
        GlassesType.TECH_SUNGLASSES -> Color(0xFF0F172A)
        GlassesType.SLIM_ARCHITECT -> Color(0xFF00C8FF)
        else -> Color(0xFF1E293B) // Dark acetate
    }

    val r = 9.5f
    // Left lens
    drawCircle(
        color = Color(0x22FFFFFF),
        radius = r,
        center = Offset(cx - spacing, eyeY)
    )
    drawCircle(
        color = frameColor,
        radius = r,
        center = Offset(cx - spacing, eyeY),
        style = Stroke(width = 1.8f)
    )

    // Right lens
    drawCircle(
        color = Color(0x22FFFFFF),
        radius = r,
        center = Offset(cx + spacing, eyeY)
    )
    drawCircle(
        color = frameColor,
        radius = r,
        center = Offset(cx + spacing, eyeY),
        style = Stroke(width = 1.8f)
    )

    // Bridge
    drawLine(
        color = frameColor,
        start = Offset(cx - spacing + r, eyeY),
        end = Offset(cx + spacing - r, eyeY),
        strokeWidth = 1.8f
    )
}

/**
 * Draws clean digital cartoon hairstyles with highlights and shadow depth.
 */
private fun DrawScope.drawHair(
    cx: Float,
    headTopY: Float,
    headCenterY: Float,
    headW: Float,
    headH: Float,
    hairStyle: HairStyle,
    hairColor: HairColor
) {
    if (hairStyle == HairStyle.BALD) return

    val baseColor = hairColor.baseColor
    val highlightColor = hairColor.highlightColor

    when (hairStyle) {
        HairStyle.SHORT_FADE -> {
            // Clean modern textured fade with sharp tapered sides
            val hairPath = Path().apply {
                moveTo(cx - headW * 0.50f, headCenterY - headH * 0.08f)
                quadraticTo(cx - headW * 0.54f, headTopY - 6f, cx, headTopY - 10f)
                quadraticTo(cx + headW * 0.54f, headTopY - 6f, cx + headW * 0.50f, headCenterY - headH * 0.08f)
                lineTo(cx + headW * 0.44f, headCenterY - headH * 0.18f)
                quadraticTo(cx, headTopY + 6f, cx - headW * 0.44f, headCenterY - headH * 0.18f)
                close()
            }
            drawPath(path = hairPath, color = baseColor)
            drawPath(path = hairPath, color = OutlineColor, style = Stroke(width = 1.4f))

            // Fade sideburns
            drawRect(
                color = baseColor.copy(alpha = 0.65f),
                topLeft = Offset(cx - headW * 0.49f, headCenterY - 4f),
                size = Size(4f, 10f)
            )
            drawRect(
                color = baseColor.copy(alpha = 0.65f),
                topLeft = Offset(cx + headW * 0.45f, headCenterY - 4f),
                size = Size(4f, 10f)
            )
        }
        HairStyle.MESSY_QUIFF -> {
            // Elevated voluminous quiff swept up
            val quiffPath = Path().apply {
                moveTo(cx - headW * 0.48f, headCenterY - headH * 0.12f)
                quadraticTo(cx - headW * 0.50f, headTopY - 14f, cx - 6f, headTopY - 18f)
                quadraticTo(cx + 8f, headTopY - 20f, cx + headW * 0.50f, headCenterY - headH * 0.10f)
                quadraticTo(cx + 6f, headTopY + 2f, cx - headW * 0.44f, headCenterY - headH * 0.18f)
                close()
            }
            drawPath(path = quiffPath, color = baseColor)
            drawPath(path = quiffPath, color = OutlineColor, style = Stroke(width = 1.4f))

            // Soft highlight streak
            drawLine(
                color = highlightColor,
                start = Offset(cx - 10f, headTopY - 10f),
                end = Offset(cx + 4f, headTopY - 14f),
                strokeWidth = 2.2f,
                cap = StrokeCap.Round
            )
        }
        HairStyle.LONG_WAVY -> {
            // Flowing wavy hair falling past shoulders
            val hairPath = Path().apply {
                moveTo(cx - headW * 0.52f, headCenterY + headH * 0.35f)
                quadraticTo(cx - headW * 0.58f, headTopY - 8f, cx, headTopY - 12f)
                quadraticTo(cx + headW * 0.58f, headTopY - 8f, cx + headW * 0.52f, headCenterY + headH * 0.35f)
                lineTo(cx + headW * 0.40f, headCenterY + headH * 0.25f)
                quadraticTo(cx + headW * 0.46f, headCenterY - headH * 0.15f, cx, headTopY + 8f)
                quadraticTo(cx - headW * 0.46f, headCenterY - headH * 0.15f, cx - headW * 0.40f, headCenterY + headH * 0.25f)
                close()
            }
            drawPath(path = hairPath, color = baseColor)
            drawPath(path = hairPath, color = OutlineColor, style = Stroke(width = 1.4f))
        }
        HairStyle.AFRO_FADE -> {
            // Clean sculpted rounded afro
            val afroPath = Path().apply {
                moveTo(cx - headW * 0.52f, headCenterY - 4f)
                quadraticTo(cx - headW * 0.62f, headTopY - 12f, cx, headTopY - 16f)
                quadraticTo(cx + headW * 0.62f, headTopY - 12f, cx + headW * 0.52f, headCenterY - 4f)
                quadraticTo(cx, headTopY + 4f, cx - headW * 0.52f, headCenterY - 4f)
                close()
            }
            drawPath(path = afroPath, color = baseColor)
            drawPath(path = afroPath, color = OutlineColor, style = Stroke(width = 1.4f))
        }
        else -> {
            // Default neat textured crop
            val cropPath = Path().apply {
                moveTo(cx - headW * 0.48f, headCenterY - headH * 0.08f)
                quadraticTo(cx - headW * 0.52f, headTopY - 8f, cx, headTopY - 10f)
                quadraticTo(cx + headW * 0.52f, headTopY - 8f, cx + headW * 0.48f, headCenterY - headH * 0.08f)
                lineTo(cx + headW * 0.40f, headCenterY - headH * 0.18f)
                quadraticTo(cx, headTopY + 6f, cx - headW * 0.40f, headCenterY - headH * 0.18f)
                close()
            }
            drawPath(path = cropPath, color = baseColor)
            drawPath(path = cropPath, color = OutlineColor, style = Stroke(width = 1.4f))
        }
    }
}

/**
 * Draws caps or beanies.
 */
private fun DrawScope.drawHat(
    cx: Float,
    headTopY: Float,
    headW: Float,
    hat: HatType
) {
    when (hat) {
        HatType.FITTED_CAP -> {
            // Obsidian baseball cap with visor
            val capPath = Path().apply {
                moveTo(cx - headW * 0.52f, headTopY + 8f)
                quadraticTo(cx, headTopY - 14f, cx + headW * 0.52f, headTopY + 8f)
                close()
            }
            drawPath(path = capPath, color = Color(0xFF0F172A))
            drawPath(path = capPath, color = OutlineColor, style = Stroke(width = 1.4f))

            // Visor bill curving forward
            val visorPath = Path().apply {
                moveTo(cx - headW * 0.48f, headTopY + 8f)
                quadraticTo(cx, headTopY + 16f, cx + headW * 0.48f, headTopY + 8f)
                lineTo(cx + headW * 0.46f, headTopY + 6f)
                lineTo(cx - headW * 0.46f, headTopY + 6f)
                close()
            }
            drawPath(path = visorPath, color = Color(0xFF1E293B))
        }
        HatType.MINIMAL_BEANIE -> {
            // Folded knit beanie
            val beaniePath = Path().apply {
                moveTo(cx - headW * 0.54f, headTopY + 10f)
                quadraticTo(cx, headTopY - 16f, cx + headW * 0.54f, headTopY + 10f)
                close()
            }
            drawPath(path = beaniePath, color = Color(0xFF334155))
            drawPath(path = beaniePath, color = OutlineColor, style = Stroke(width = 1.4f))
        }
        else -> {}
    }
}
