package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AmbientLockScreen(
    activeMission: TaskEntity?,
    profileName: String = "Alex",
    onUnlock: () -> Unit,
    onExecuteMission: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000L)
        }
    }

    // Gentle pulsing animation for the unlock hint arrow
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val arrowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrowOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .draggable(
                state = rememberDraggableState { delta ->
                    if (delta < -20) {
                        onUnlock()
                    }
                },
                orientation = Orientation.Vertical
            )
            .testTag("ambient_lock_screen")
    ) {
        // Mountain Backdrop
        MountainMistBackdrop()

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Date & Huge Aesthetic Clock
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = if (currentDate.isNotBlank()) currentDate else "Mon, 22 Sep",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (currentTime.isNotBlank()) currentTime else "09:27",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 84.sp,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = (-2).sp
                    ),
                    color = TextPureWhite,
                    modifier = Modifier.testTag("ambient_clock")
                )
            }

            // Middle Space with Intentional Philosophy Quote and Personalized Greeting
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 16.dp)
            ) {
                if (profileName.isNotBlank()) {
                    Text(
                        text = "Good day, $profileName",
                        style = MaterialTheme.typography.titleSmall,
                        color = CrimsonElectric.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = "“A focused mind builds an extraordinary life.”",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Section: Today's Mission & Unlock Pill
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Today's Mission Minimal Card (as shown on Screen 4)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(AtlasCard.copy(alpha = 0.85f))
                        .border(1.dp, AtlasBorder, RoundedCornerShape(22.dp))
                        .clickable { onExecuteMission() }
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                        .testTag("ambient_mission_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Today's Mission",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = activeMission?.title ?: "Build the habit.",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPureWhite,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        // Soft Sage Circular Execution Action
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SoftSageGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Execute",
                                tint = AtlasCard,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Swipe to Start / Unlock Hint (as shown in Screen 12 & 4)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onUnlock() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("unlock_hint_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Swipe to start your day",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Unlock",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(14.dp)
                            .graphicsLayer { translationX = arrowOffset }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "“Small steps everyday.”",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
