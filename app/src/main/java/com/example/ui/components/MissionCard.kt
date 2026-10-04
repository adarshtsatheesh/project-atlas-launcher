package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.AtlasCardElevated
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.CrimsonExecuteButtonBrush
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassCardBackgroundBrush
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSand

@Composable
fun MissionCard(
    mission: TaskEntity?,
    onStartFocus: () -> Unit,
    onCompleteMission: (TaskEntity) -> Unit,
    onToggleMicroStep: (TaskEntity, Int) -> Unit,
    onAddMicroStep: (TaskEntity, String) -> Unit,
    onOpenTaskEngine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = com.example.ui.theme.LocalAtlasAccent.current
    if (mission == null) {
        // Empty State: Prompt user to set an intentional mission with Glassmorphism
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(GlassCardBackgroundBrush)
                .border(1.dp, GlassBorderBrush, RoundedCornerShape(26.dp))
                .clickable { onOpenTaskEngine() }
                .padding(22.dp)
                .testTag("empty_mission_card")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TODAY'S MISSION",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftSageGreen,
                        letterSpacing = 1.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Open Missions",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Choose your ONE target",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPureWhite,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Execution starts by eliminating ambiguity. Choose a single priority.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onOpenTaskEngine,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AtlasCardElevated,
                        contentColor = SoftSageGreen
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("define_mission_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Mission", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        return
    }

    var showAddStepInput by remember { mutableStateOf(false) }
    var newStepText by remember { mutableStateOf("") }
    val microSteps = mission.getMicroSteps()
    val completedStepsCount = microSteps.count { it.second }
    val totalSteps = microSteps.size
    val progressRatio = if (totalSteps > 0) completedStepsCount.toFloat() / totalSteps else 0.5f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(GlassCardBackgroundBrush)
            .border(1.dp, GlassBorderBrush, RoundedCornerShape(28.dp))
            .padding(22.dp)
            .testTag("active_mission_card")
    ) {
        Column {
            // Header Row: "TODAY'S MISSION" & Estimated duration badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenTaskEngine() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SoftSageGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TODAY'S MISSION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        ),
                        color = SoftSageGreen
                    )
                }

                // Estimated time badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AtlasCardElevated)
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    val hours = mission.estimatedMinutes / 60
                    val mins = mission.estimatedMinutes % 60
                    val durationText = when {
                        hours > 0 && mins > 0 -> "${hours}h ${mins}m"
                        hours > 0 -> "${hours}h"
                        else -> "${mins}m"
                    }
                    Text(
                        text = durationText,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Large Task Title
            Text(
                text = mission.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    lineHeight = 30.sp
                ),
                color = TextPureWhite,
                modifier = Modifier.testTag("mission_title")
            )

            if (mission.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = mission.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Indicator (Minimal bar with percentage)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (totalSteps > 0) "$completedStepsCount of $totalSteps subtasks completed" else "Active sprint in progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${(progressRatio * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = SoftSageGreen,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = SoftSageGreen,
                    trackColor = AtlasCardElevated
                )
            }

            // Micro-Steps / Subtasks preview
            if (microSteps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AtlasCardElevated.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    microSteps.take(3).forEachIndexed { index, (stepTitle, isDone) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleMicroStep(mission, index) }
                                .padding(vertical = 5.dp)
                                .testTag("micro_step_$index"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDone) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isDone) SoftSageGreen else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stepTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDone) TextMuted else TextPureWhite,
                                textDecoration = if (isDone) TextDecoration.LineThrough else null,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Inline input to add next micro-step
            AnimatedVisibility(
                visible = showAddStepInput,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newStepText,
                        onValueChange = { newStepText = it },
                        placeholder = { Text("e.g. Export assets, write draft...", color = TextMuted, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftSageGreen,
                            unfocusedBorderColor = AtlasBorder,
                            focusedTextColor = TextPureWhite,
                            unfocusedTextColor = TextPureWhite,
                            cursorColor = SoftSageGreen
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_step_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newStepText.isNotBlank()) {
                                onAddMicroStep(mission, newStepText)
                                newStepText = ""
                                showAddStepInput = false
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SoftSageGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Add Step",
                            tint = AtlasBackground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // One Large Execute Button (Screen signature action with Glassmorphism!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CrimsonExecuteButtonBrush)
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(Color(0x99FFFFFF), accent.copy(alpha = 0.2f))
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onStartFocus() }
                    .testTag("start_focus_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Execute Mission",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = TextPureWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Execute",
                        tint = TextPureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Actions: + Next Step & Mark Done
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showAddStepInput = !showAddStepInput }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = SoftSageGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add micro-step",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftSageGreen
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCompleteMission(mission) }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("complete_mission_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mark complete",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
