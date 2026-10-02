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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.AtlasCardElevated
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassCardBackgroundBrush
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSand

@Composable
fun TodayTasksSection(
    tasks: List<TaskEntity>,
    onToggleTask: (TaskEntity) -> Unit,
    onSetAsMission: (Long) -> Unit,
    onAddTask: (String, Int) -> Unit,
    onOpenAllMissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuickAdd by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskMinutes by remember { mutableStateOf(20) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .testTag("today_tasks_section")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Today's Tasks",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp
                    ),
                    color = TextPureWhite
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${tasks.size})",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Add Quick Task Button
                IconButton(
                    onClick = { showQuickAdd = !showQuickAdd },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AtlasCardElevated)
                        .testTag("quick_add_task_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        tint = SoftSageGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "All",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenAllMissions() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
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
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    placeholder = { Text("Task name...", color = TextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftSageGreen,
                        unfocusedBorderColor = AtlasBorder,
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite,
                        cursorColor = SoftSageGreen
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            onAddTask(newTaskTitle, newTaskMinutes)
                            newTaskTitle = ""
                            showQuickAdd = false
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SoftSageGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save Task",
                        tint = AtlasBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Task Items List (Clean minimal glassmorphic rounded cards with checkbox and duration)
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(GlassCardBackgroundBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(18.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "All today's tasks completed. Rest or start next focus.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasks.take(4).forEach { task ->
                    TaskRowItem(
                        task = task,
                        onToggle = { onToggleTask(task) },
                        onSetAsMission = { onSetAsMission(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskRowItem(
    task: TaskEntity,
    onToggle: () -> Unit,
    onSetAsMission: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(GlassCardBackgroundBrush)
            .border(1.dp, GlassBorderBrush, RoundedCornerShape(18.dp))
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 13.dp)
            .testTag("task_row_${task.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minimal Checkbox with Electric Crimson Accent
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = if (task.isCompleted) "Completed" else "Incomplete",
                tint = if (task.isCompleted) com.example.ui.theme.LocalAtlasAccent.current else TextMuted,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and description
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = if (task.isCompleted) TextMuted else TextPureWhite,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Estimated Duration Tag (e.g. "15m", "30m")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AtlasCardElevated)
                .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "${task.estimatedMinutes}m",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
