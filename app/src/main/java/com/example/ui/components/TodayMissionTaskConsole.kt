package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCardElevated
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.CrimsonExecuteButtonBrush
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassCardBackgroundBrush
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary

@Composable
fun TodayMissionTaskConsole(
    activeMission: TaskEntity?,
    tasks: List<TaskEntity>,
    onStartFocus: () -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onSetAsMission: (Long) -> Unit,
    onQuickAddTask: (String) -> Unit,
    onOpenCalendarPlanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuickAdd by remember { mutableStateOf(false) }
    var quickTaskTitle by remember { mutableStateOf("") }

    val completedCount = remember(tasks) { tasks.count { it.isCompleted } }
    val effectiveMission = remember(activeMission, tasks) {
        activeMission ?: tasks.firstOrNull { !it.isCompleted }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(GlassCardBackgroundBrush)
            .border(1.dp, GlassBorderBrush, RoundedCornerShape(26.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // Absorb double-tap inside the tasks area so it does not trigger phone lock
                    }
                )
            }
            .padding(18.dp)
            .testTag("today_mission_task_console")
    ) {
        Column {
            // Header Row: TODAY'S FOCUS, task count, Calendar button & Quick Add
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(CrimsonElectric)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TODAY'S TASKS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        ),
                        color = CrimsonElectric
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${completedCount}/${tasks.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                // Action Icons: Calendar Planner & Quick Add
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Open Calendar Planner
                    IconButton(
                        onClick = onOpenCalendarPlanner,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AtlasCardElevated)
                            .testTag("open_calendar_planner_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Open Calendar",
                            tint = TextPureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Quick Add Task
                    IconButton(
                        onClick = { showQuickAdd = !showQuickAdd },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AtlasCardElevated)
                            .testTag("toggle_quick_add_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task for Today",
                            tint = CrimsonElectric,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Quick Add Input field
            AnimatedVisibility(
                visible = showQuickAdd,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quickTaskTitle,
                        onValueChange = { quickTaskTitle = it },
                        placeholder = { Text("What to do today...", color = TextMuted, fontSize = 13.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonElectric,
                            unfocusedBorderColor = AtlasBorder,
                            focusedTextColor = TextPureWhite,
                            unfocusedTextColor = TextPureWhite,
                            cursorColor = CrimsonElectric
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_task_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (quickTaskTitle.isNotBlank()) {
                                onQuickAddTask(quickTaskTitle)
                                quickTaskTitle = ""
                                showQuickAdd = false
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CrimsonElectric)
                            .testTag("confirm_quick_add_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirm",
                            tint = TextPureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Task List (Minimal rows with easy tick checkboxes)
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AtlasCardElevated.copy(alpha = 0.5f))
                        .clickable { onOpenCalendarPlanner() }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks for today. Tap to schedule in Calendar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tasks.forEach { task ->
                        val isMission = task.id == effectiveMission?.id && !task.isCompleted
                        MinimalTaskRow(
                            task = task,
                            isMission = isMission,
                            onToggle = { onToggleTask(task) },
                            onSetAsMission = { onSetAsMission(task.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Minimal "Execute Mission" Button
            if (effectiveMission != null && !effectiveMission.isCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CrimsonExecuteButtonBrush)
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(Color(0x99FFFFFF), Color(0x33FF2A4D))
                            ),
                            RoundedCornerShape(14.dp)
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
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = TextPureWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Execute",
                            tint = TextPureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                // All missions complete state
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AtlasCardElevated)
                        .clickable { onOpenCalendarPlanner() }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "All today's tasks completed ✓",
                        style = MaterialTheme.typography.bodySmall,
                        color = CrimsonElectric,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Plan ahead",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun MinimalTaskRow(
    task: TaskEntity,
    isMission: Boolean,
    onToggle: () -> Unit,
    onSetAsMission: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isMission) AtlasCardElevated.copy(alpha = 0.9f)
                else AtlasCardElevated.copy(alpha = 0.4f)
            )
            .border(
                1.dp,
                if (isMission) CrimsonElectric.copy(alpha = 0.6f) else Color(0x1AFFFFFF),
                RoundedCornerShape(14.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("task_item_${task.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ticking Checkbox with instant feedback
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = if (task.isCompleted) "Completed" else "Incomplete",
                tint = if (task.isCompleted) CrimsonElectric else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Task Title
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isMission) FontWeight.Medium else FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = if (task.isCompleted) TextMuted else TextPureWhite,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Estimated Duration
        Text(
            text = "${task.estimatedMinutes}m",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Star to toggle as active mission
        IconButton(
            onClick = onSetAsMission,
            modifier = Modifier.size(26.dp)
        ) {
            Icon(
                imageVector = if (isMission) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = if (isMission) "Active Mission" else "Set as Mission",
                tint = if (isMission) CrimsonElectric else TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
