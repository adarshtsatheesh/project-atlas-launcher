package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassCardBackgroundBrush
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSand
import com.example.ui.viewmodel.ClockStyle
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AtlasClockHeader(
    clockStyle: ClockStyle = ClockStyle.NEO_GLASS,
    onToggleClockStyle: () -> Unit = {},
    onOpenGestureOverview: () -> Unit,
    onLockScreen: () -> Unit = {},
    onOpenAmbientLock: () -> Unit = onLockScreen,
    onOpenProgressStats: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hours by remember { mutableStateOf("09") }
    var minutes by remember { mutableStateOf("41") }
    var seconds by remember { mutableStateOf("00") }
    var amPm by remember { mutableStateOf("AM") }
    var currentDate by remember { mutableStateOf("Friday, Sep 25") }
    var rawSeconds by remember { mutableFloatStateOf(0f) }
    var rawMinutes by remember { mutableFloatStateOf(0f) }
    var rawHours by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(clockStyle) {
        val hourFormat = SimpleDateFormat("HH", Locale.getDefault())
        val minFormat = SimpleDateFormat("mm", Locale.getDefault())
        val secFormat = SimpleDateFormat("ss", Locale.getDefault())
        val amPmFormat = SimpleDateFormat("a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())

        val isChronograph = clockStyle == ClockStyle.CHRONOGRAPH
        val updateInterval = if (isChronograph) 100L else 500L
        var lastSec = -1

        while (true) {
            val cal = Calendar.getInstance()
            val nowSec = cal.get(Calendar.SECOND)

            if (isChronograph) {
                val ms = cal.get(Calendar.MILLISECOND)
                rawSeconds = nowSec + ms / 1000f
                rawMinutes = cal.get(Calendar.MINUTE) + rawSeconds / 60f
                rawHours = (cal.get(Calendar.HOUR) % 12) + rawMinutes / 60f
            }

            if (nowSec != lastSec || isChronograph) {
                val now = cal.time
                hours = hourFormat.format(now)
                minutes = minFormat.format(now)
                seconds = secFormat.format(now)
                amPm = amPmFormat.format(now).uppercase()
                currentDate = dateFormat.format(now)
                lastSec = nowSec
            }

            delay(updateInterval)
        }
    }

    // Infinite breathing glow for the colon and crimson accents
    val infiniteTransition = rememberInfiniteTransition(label = "clock_pulse")
    val pulseAlphaState = infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colon_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .testTag("atlas_clock_header")
    ) {
        // Top System Action Bar: Weather + Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Weather / Location pill with glassmorphic finish
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassCardBackgroundBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(16.dp))
                    .padding(horizontal = 11.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.WbSunny,
                    contentDescription = "Weather",
                    tint = CrimsonElectric,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "21°C • Clear",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPureWhite,
                    fontSize = 11.sp
                )
            }

            // Top action icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Gesture Overview Shortcut
                IconButton(
                    onClick = onOpenGestureOverview,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GlassCardBackgroundBrush)
                        .border(1.dp, GlassBorderBrush, CircleShape)
                        .testTag("header_gesture_overview_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Gesture,
                        contentDescription = "Gesture System",
                        tint = CrimsonElectric,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Stats shortcut
                IconButton(
                    onClick = onOpenProgressStats,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GlassCardBackgroundBrush)
                        .border(1.dp, GlassBorderBrush, CircleShape)
                        .testTag("header_stats_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BarChart,
                        contentDescription = "Progress",
                        tint = TextPureWhite,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Screen Lock shortcut (with long-press to open Ambient Display)
                IconButton(
                    onClick = onOpenAmbientLock,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GlassCardBackgroundBrush)
                        .border(1.dp, GlassBorderBrush, CircleShape)
                        .testTag("header_lock_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Ambient Lock Screen",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Settings shortcut
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GlassCardBackgroundBrush)
                        .border(1.dp, GlassBorderBrush, CircleShape)
                        .testTag("header_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Clock Visual Unit (tap to cycle styles)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onToggleClockStyle() }
                .testTag("atlas_large_clock")
        ) {
            AnimatedContent(
                targetState = clockStyle,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                label = "clock_style_animation"
            ) { style ->
                when (style) {
                    ClockStyle.NEO_GLASS -> {
                        NeoGlassDigitalClock(
                            hours = hours,
                            minutes = minutes,
                            seconds = seconds,
                            amPm = amPm,
                            pulseAlpha = { pulseAlphaState.value }
                        )
                    }
                    ClockStyle.STACKED_DIGITAL -> {
                        StackedDigitalClock(
                            hours = hours,
                            minutes = minutes,
                            seconds = seconds,
                            pulseAlpha = { pulseAlphaState.value }
                        )
                    }
                    ClockStyle.CHRONOGRAPH -> {
                        ChronographClock(
                            rawHours = rawHours,
                            rawMinutes = rawMinutes,
                            rawSeconds = rawSeconds,
                            digitalTime = "$hours:$minutes"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Current Date with subtle glass container
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("atlas_current_date")
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(CrimsonElectric)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = currentDate,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                ),
                color = TextPureWhite
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Small Motivational Quote
        Text(
            text = "“Energy flows where attention goes.”",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            color = TextSecondary,
            modifier = Modifier.testTag("atlas_quote")
        )
    }
}

/**
 * Style 1: Neo-Glass Digital Clock
 * Sleek dual-tone digits with a glowing crimson pulsing colon and live seconds tag
 */
@Composable
private fun NeoGlassDigitalClock(
    hours: String,
    minutes: String,
    seconds: String,
    amPm: String,
    pulseAlpha: () -> Float
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Hour (Crisp Pure White Glass)
        Text(
            text = hours,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraLight,
                fontSize = 72.sp,
                letterSpacing = (-2).sp
            ),
            color = TextPureWhite
        )

        // Pulsing Neon Crimson Colon (graphicsLayer prevents full-clock recompositions)
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .height(58.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .graphicsLayer { alpha = pulseAlpha() }
                        .clip(CircleShape)
                        .background(CrimsonElectric)
                )
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .graphicsLayer { alpha = pulseAlpha() }
                        .clip(CircleShape)
                        .background(CrimsonElectric)
                )
            }
        }

        // Minutes (Striking Crimson Glow Accent)
        Text(
            text = minutes,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraLight,
                fontSize = 72.sp,
                letterSpacing = (-2).sp
            ),
            color = CrimsonElectric
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Seconds & AM/PM Column Pill
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Live Seconds Chip with Glass Rim
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GlassCardBackgroundBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = ":$seconds",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    ),
                    color = CrimsonElectric
                )
            }

            // AM / PM tag
            Text(
                text = amPm,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                ),
                color = TextSecondary
            )
        }
    }
}

/**
 * Style 2: Stacked Digital Lockscreen Clock
 * Large vertical stacked numerals (Nothing OS / Android 15 inspired)
 */
@Composable
private fun StackedDigitalClock(
    hours: String,
    minutes: String,
    seconds: String,
    pulseAlpha: () -> Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            // Hours
            Text(
                text = hours,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Light,
                    fontSize = 58.sp,
                    lineHeight = 56.sp,
                    letterSpacing = (-1).sp
                ),
                color = TextPureWhite
            )

            // Minutes
            Text(
                text = minutes,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Light,
                    fontSize = 58.sp,
                    lineHeight = 56.sp,
                    letterSpacing = (-1).sp
                ),
                color = CrimsonElectric
            )
        }

        // Side badge indicator
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(GlassCardBackgroundBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .graphicsLayer { alpha = pulseAlpha() }
                            .clip(CircleShape)
                            .background(CrimsonElectric)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SEC $seconds",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = TextPureWhite
                    )
                }
            }

            Text(
                text = "STACKED OS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontSize = 9.sp
                ),
                color = TextMuted
            )
        }
    }
}

/**
 * Style 3: Precision Glass Chronograph Clock
 * Modern minimalist analog dial with illuminated second-sweep and glass dial
 */
@Composable
private fun ChronographClock(
    rawHours: Float,
    rawMinutes: Float,
    rawSeconds: Float,
    digitalTime: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GlassCardBackgroundBrush)
            .border(1.dp, GlassBorderBrush, RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Analog Dial
        val clockAccent = CrimsonElectric
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(80.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f - 4.dp.toPx()

                // Dial Outer Ring
                drawCircle(
                    color = Color(0x33FFFFFF),
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                )

                // 12, 3, 6, 9 Cardinal Tick Marks
                for (i in 0 until 12) {
                    val angleDeg = i * 30.0
                    val angleRad = Math.toRadians(angleDeg)
                    val isCardinal = i % 3 == 0
                    val tickLen = if (isCardinal) 6.dp.toPx() else 3.dp.toPx()
                    val tickColor = if (isCardinal) clockAccent else Color(0x66FFFFFF)

                    val startX = center.x + (radius - tickLen) * sin(angleRad).toFloat()
                    val startY = center.y - (radius - tickLen) * cos(angleRad).toFloat()
                    val endX = center.x + radius * sin(angleRad).toFloat()
                    val endY = center.y - radius * cos(angleRad).toFloat()

                    drawLine(
                        color = tickColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (isCardinal) 2.dp.toPx() else 1.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Hour Hand
                val hourAngleRad = Math.toRadians((rawHours / 12f * 360.0))
                val hourLen = radius * 0.50f
                drawLine(
                    color = Color.White,
                    start = center,
                    end = Offset(
                        center.x + hourLen * sin(hourAngleRad).toFloat(),
                        center.y - hourLen * cos(hourAngleRad).toFloat()
                    ),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Minute Hand
                val minAngleRad = Math.toRadians((rawMinutes / 60f * 360.0))
                val minLen = radius * 0.72f
                drawLine(
                    color = Color(0xEEFFFFFF),
                    start = center,
                    end = Offset(
                        center.x + minLen * sin(minAngleRad).toFloat(),
                        center.y - minLen * cos(minAngleRad).toFloat()
                    ),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Second Hand (Electric Crimson neon)
                val secAngleRad = Math.toRadians((rawSeconds / 60f * 360.0))
                val secLen = radius * 0.82f
                drawLine(
                    color = clockAccent,
                    start = center,
                    end = Offset(
                        center.x + secLen * sin(secAngleRad).toFloat(),
                        center.y - secLen * cos(secAngleRad).toFloat()
                    ),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Center Pin
                drawCircle(
                    color = clockAccent,
                    radius = 3.dp.toPx(),
                    center = center
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Digital Companion Readout
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = digitalTime,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Light,
                    fontSize = 38.sp,
                    letterSpacing = (-1).sp
                ),
                color = TextPureWhite
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(CrimsonElectric)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CHRONOMETER PRECISION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    color = TextSecondary
                )
            }
        }
    }
}
