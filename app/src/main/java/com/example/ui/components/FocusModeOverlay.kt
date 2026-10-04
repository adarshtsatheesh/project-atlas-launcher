package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.AtlasCardElevated
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.SoftSageGreenGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSand
import com.example.ui.viewmodel.FocusPreset
import com.example.util.AmbientSoundPlayer
import java.util.Locale

@Composable
fun FocusModeOverlay(
    activeMission: TaskEntity?,
    timerSecondsRemaining: Int,
    initialTimerSeconds: Int,
    isTimerRunning: Boolean,
    selectedPreset: FocusPreset,
    isBreathingActive: Boolean,
    ambientSoundEnabled: Boolean = true,
    exitFrictionEnabled: Boolean = true,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSelectPreset: (FocusPreset) -> Unit,
    onToggleBreathing: () -> Unit,
    onToggleMicroStep: (TaskEntity, Int) -> Unit,
    onFinishSession: (completed: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showExitDialog by remember { mutableStateOf(false) }
    var isLofiPlaying by remember { mutableStateOf(ambientSoundEnabled) }
    var selectedSoundTrack by remember { mutableStateOf(AmbientSoundPlayer.Track.FOREST) }

    // Real procedural ambience synthesised on a background thread (no audio assets)
    val ambientPlayer = remember { AmbientSoundPlayer() }
    DisposableEffect(Unit) {
        onDispose { ambientPlayer.release() }
    }
    LaunchedEffect(ambientSoundEnabled, isLofiPlaying, selectedSoundTrack) {
        if (ambientSoundEnabled && isLofiPlaying) {
            ambientPlayer.start(selectedSoundTrack)
        } else {
            ambientPlayer.stop()
        }
    }

    // Mindful Exit Friction: gate early exits behind a breathing prompt when enabled
    val requestExit: () -> Unit = {
        if (exitFrictionEnabled) showExitDialog = true else onFinishSession(false)
    }

    BackHandler(enabled = true) { requestExit() }

    // Breathing pulse animation (4-second inhale, 4-second exhale)
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    val progress = if (initialTimerSeconds > 0) {
        (initialTimerSeconds - timerSecondsRemaining).toFloat() / initialTimerSeconds.toFloat()
    } else 0f

    val minutes = timerSecondsRemaining / 60
    val seconds = timerSecondsRemaining % 60
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("focus_mode_overlay")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Back Arrow & "Focus" title (Screen 7 layout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { requestExit() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AtlasCard)
                        .border(1.dp, AtlasBorder, CircleShape)
                        .testTag("close_focus_mode_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit Focus",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Focus",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPureWhite,
                    fontWeight = FontWeight.Medium
                )

                // Invisible spacer for balance
                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Philosophy quote (Screen 7: "“Deep work builds freedom.”")
            Text(
                text = "“Deep work builds freedom.”",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Giant Circular Glowing Countdown Timer (Screen 7 layout)
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Gentle breathing ambient aura
                if (isTimerRunning) {
                    Box(
                        modifier = Modifier
                            .size(230.dp)
                            .graphicsLayer(
                                scaleX = breathingScale,
                                scaleY = breathingScale,
                                alpha = 0.2f
                            )
                            .background(SoftSageGreenGlow, CircleShape)
                    )
                }

                // Custom Circular Progress Ring
                val timerAccent = SoftSageGreen
                Canvas(modifier = Modifier.size(210.dp)) {
                    val strokeWidth = 3.dp.toPx()
                    // Background track
                    drawCircle(
                        color = AtlasBorder,
                        radius = size.minDimension / 2 - strokeWidth,
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress arc
                    drawArc(
                        color = timerAccent,
                        startAngle = -90f,
                        sweepAngle = 360f * (1f - progress),
                        useCenter = false,
                        style = Stroke(width = strokeWidth + 1.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner Timer Text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-1).sp
                        ),
                        color = TextPureWhite,
                        modifier = Modifier.testTag("focus_timer_text")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isTimerRunning) "FOCUS" else "PAUSED",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isTimerRunning) SoftSageGreen else TextMuted,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Preset Selectors (10m, 25m, 50m, 90m)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                FocusPreset.entries.forEach { preset ->
                    val isSelected = selectedPreset == preset
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) AtlasCardElevated else AtlasCard)
                            .border(
                                1.dp,
                                if (isSelected) SoftSageGreen else AtlasBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectPreset(preset) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "${preset.minutes}m",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) SoftSageGreen else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lo-fi Ambient Sound Bar (Screen 7 layout: "Lo-fi Focus • Forest")
            if (ambientSoundEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(AtlasCard)
                    .border(1.dp, AtlasBorder, RoundedCornerShape(20.dp))
                    .clickable {
                        val tracks = AmbientSoundPlayer.Track.entries
                        val nextIndex = (tracks.indexOf(selectedSoundTrack) + 1) % tracks.size
                        selectedSoundTrack = tracks[nextIndex]
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("lofi_sound_bar")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = if (isLofiPlaying) SoftSageGreen else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Lo-fi Focus • ${selectedSoundTrack.label}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPureWhite,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = if (isLofiPlaying) "Playing ambient frequency" else "Paused",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { isLofiPlaying = !isLofiPlaying },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isLofiPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Toggle Ambient Sound",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Active Mission Checklist Card
            if (activeMission != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(AtlasCard)
                        .border(1.dp, AtlasBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "TARGET MISSION",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activeMission.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPureWhite
                    )

                    val microSteps = activeMission.getMicroSteps()
                    if (microSteps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        microSteps.forEachIndexed { index, (stepText, done) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleMicroStep(activeMission, index) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (done) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (done) SoftSageGreen else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stepText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (done) TextMuted else TextPureWhite,
                                    textDecoration = if (done) TextDecoration.LineThrough else null,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Warm Sand Pill Button: "Start" / "Pause" (Screen 7 layout)
            Button(
                onClick = {
                    if (isTimerRunning) onPause() else onResume()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarmSand,
                    contentColor = AtlasBackground
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(52.dp)
                    .testTag("pause_resume_button")
            ) {
                Text(
                    text = if (isTimerRunning) "Pause" else "Start",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp
                    ),
                    color = AtlasBackground
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary mark complete link
            TextButton(
                onClick = { onFinishSession(true) },
                modifier = Modifier.testTag("finish_focus_button")
            ) {
                Text(
                    text = "Finish & Record Session",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }

    // Behavioral Psychology Exit Friction Dialog (only when the setting is on)
    if (showExitDialog && exitFrictionEnabled) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            containerColor = Color(0xF5101218),
            title = {
                Text(
                    text = "Pause & Reflect",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPureWhite
                )
            },
            text = {
                Text(
                    text = "Resistance is normal before entering flow. Take a conscious breath. Do you want to step away or complete the sprint?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showExitDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.ui.theme.LocalAtlasAccent.current,
                        contentColor = Color.White
                    )
                ) {
                    Text("Stay Focused")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onFinishSession(false)
                    }
                ) {
                    Text("End Session", color = TextMuted)
                }
            }
        )
    }
}

