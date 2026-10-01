package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import kotlin.math.abs
import kotlin.math.roundToInt

val AlphabetLetters: List<Char> = ('A'..'Z').toList() + listOf('#')

/**
 * Precomputed Alphabet Index structure for O(1) lookups and guaranteed
 * scrollability across all letters from A through Z.
 */
class AlphabetIndexData(
    private val alphabetToItemMap: Map<Char, Int>,
    private val itemToLetterArray: Array<Char>,
    val availableLetters: Set<Char>
) {
    fun findTargetIndex(targetChar: Char): Int {
        // 1. Direct O(1) hit
        alphabetToItemMap[targetChar]?.let { return it }

        // 2. Nearest subsequent available letter (e.g. Q -> R, S...)
        if (targetChar in 'A'..'Z') {
            for (c in targetChar..'Z') {
                alphabetToItemMap[c]?.let { return it }
            }
            // Check '#' if available
            alphabetToItemMap['#']?.let { return it }
        }

        // 3. Fallback: nearest preceding available letter (e.g. Z -> Y, X, W...)
        if (targetChar in 'A'..'Z') {
            for (c in targetChar downTo 'A') {
                alphabetToItemMap[c]?.let { return it }
            }
        } else if (targetChar == '#') {
            for (c in 'Z' downTo 'A') {
                alphabetToItemMap[c]?.let { return it }
            }
        }

        return 0
    }

    fun getLetterForIndex(itemIndex: Int): Char {
        if (itemToLetterArray.isEmpty()) return 'A'
        val clamped = itemIndex.coerceIn(0, itemToLetterArray.lastIndex)
        return itemToLetterArray[clamped]
    }

    companion object {
        val Empty = AlphabetIndexData(emptyMap(), emptyArray(), emptySet())
    }
}

/**
 * Flagship-grade Alphabet Fast Scroller with continuous magnification physics,
 * floating glassmorphic indicator popup, and 120Hz haptic feedback.
 */
@Composable
fun AlphabetFastScroller(
    availableLetters: Set<Char>,
    currentSelectedLetter: Char?,
    accentColor: Color,
    onLetterSelected: (Char, Boolean) -> Unit, // (letter, isDragging)
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    var isInteracting by remember { mutableStateOf(false) }
    var touchYPx by remember { mutableFloatStateOf(0f) }
    var railHeightPx by remember { mutableFloatStateOf(0f) }
    var lastHapticLetter by remember { mutableStateOf<Char?>(null) }
    var activePopupLetter by remember { mutableStateOf('A') }
    var hoverFloatIndex by remember { mutableFloatStateOf(0f) }

    // Smooth floating popup vertical interpolation using spring physics
    val animatedPopupYPx by animateFloatAsState(
        targetValue = touchYPx,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "fast_scroller_popup_y"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(vertical = 8.dp)
            .testTag("alphabet_fast_scroller")
    ) {
        // 1. Floating Glassmorphic Letter Popup Indicator
        AnimatedVisibility(
            visible = isInteracting,
            enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) + scaleIn(spring(stiffness = Spring.StiffnessMedium), initialScale = 0.7f),
            exit = fadeOut(spring(stiffness = Spring.StiffnessMedium)) + scaleOut(spring(stiffness = Spring.StiffnessMedium), targetScale = 0.7f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset {
                    val popupHeightPx = with(density) { 58.dp.toPx() }
                    val offsetY = (animatedPopupYPx - popupHeightPx / 2f)
                        .coerceIn(0f, (railHeightPx - popupHeightPx).coerceAtLeast(0f))
                    IntOffset(
                        x = with(density) { (-54).dp.roundToPx() },
                        y = offsetY.roundToInt()
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = accentColor.copy(alpha = 0.4f),
                        spotColor = accentColor
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xF0121218))
                    .border(1.5.dp, accentColor.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = activePopupLetter.toString(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = TextPureWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                }
            }
        }

        // 2. Vertical Alphabet Rail with Dynamic Magnification
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(30.dp)
                .padding(vertical = 4.dp)
                .onGloballyPositioned { coordinates ->
                    railHeightPx = coordinates.size.height.toFloat()
                }
                .pointerInput(AlphabetLetters) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        isInteracting = true

                        fun handleTouchPosition(y: Float, isDrag: Boolean) {
                            if (railHeightPx <= 0f) return
                            val clampedY = y.coerceIn(0f, railHeightPx - 1f)
                            val fraction = clampedY / railHeightPx
                            val floatIdx = (fraction * AlphabetLetters.size).coerceIn(0f, AlphabetLetters.lastIndex.toFloat())
                            val letterIndex = floatIdx.toInt().coerceIn(0, AlphabetLetters.lastIndex)
                            val letter = AlphabetLetters[letterIndex]

                            hoverFloatIndex = floatIdx
                            touchYPx = clampedY
                            activePopupLetter = letter

                            if (letter != lastHapticLetter) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                lastHapticLetter = letter
                            }

                            onLetterSelected(letter, isDrag)
                        }

                        // Immediate response on tap
                        handleTouchPosition(down.position.y, isDrag = false)

                        // Fluid continuous drag
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: break
                            if (!change.pressed) {
                                isInteracting = false
                                lastHapticLetter = null
                                break
                            }
                            change.consume()
                            handleTouchPosition(change.position.y, isDrag = true)
                        }
                    }
                }
                .semantics {
                    contentDescription = "Alphabet Fast Scroller"
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AlphabetLetters.forEachIndexed { index, letter ->
                val hasApps = letter in availableLetters
                val isCurrentSelection = letter == (if (isInteracting) activePopupLetter else currentSelectedLetter)

                // Compute dynamic magnification based on distance from the touch point
                val distance = if (isInteracting) abs(hoverFloatIndex - index) else Float.MAX_VALUE

                val targetScale = when {
                    !isInteracting && isCurrentSelection -> 1.25f
                    distance <= 0.6f -> 1.70f // Focused letter
                    distance <= 1.4f -> 1.35f // Immediate neighbours
                    distance <= 2.2f -> 1.15f // Secondary neighbours
                    else -> 1.0f
                }

                val animatedScale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = spring(stiffness = Spring.StiffnessHigh, dampingRatio = Spring.DampingRatioNoBouncy),
                    label = "letter_magnify_${letter}"
                )

                val targetAlpha = when {
                    isCurrentSelection -> 1.0f
                    isInteracting && distance <= 1.4f -> 0.90f
                    hasApps -> 0.65f
                    else -> 0.28f
                }

                val animatedAlpha by animateFloatAsState(
                    targetValue = targetAlpha,
                    animationSpec = spring(stiffness = Spring.StiffnessHigh),
                    label = "letter_alpha_${letter}"
                )

                val textColor = when {
                    isCurrentSelection -> TextPureWhite
                    hasApps -> TextSecondary
                    else -> Color(0x44FFFFFF)
                }

                val fontWeight = when {
                    isCurrentSelection -> FontWeight.ExtraBold
                    hasApps -> FontWeight.SemiBold
                    else -> FontWeight.Light
                }

                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .scale(animatedScale),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCurrentSelection && !isInteracting) {
                        // Soft active glow pill when idle
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.35f))
                        )
                    }

                    Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = fontWeight,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = textColor.copy(alpha = animatedAlpha),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Section Header for each Alphabet group in the App Drawer grid.
 */
@Composable
fun AlphabetSectionHeader(
    letter: Char,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 8.dp, start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = TextPureWhite
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.35f),
                            Color(0x18FFFFFF),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
