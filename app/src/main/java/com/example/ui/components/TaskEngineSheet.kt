package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.AtlasAmber
import com.example.ui.theme.CarbonBorder
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CarbonSurfaceElevated
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TaskEngineSheet(
    activeTasks: List<TaskEntity>,
    completedTasks: List<TaskEntity>,
    activeMissionId: Long?,
    onSetMission: (Long) -> Unit,
    onToggleCompletion: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onCreateTask: (title: String, description: String, priority: Int, estimatedMinutes: Int, microSteps: List<String>, asMission: Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isAddingTask by remember { mutableStateOf(false) }

    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }
    var newTaskSteps by remember { mutableStateOf("") }
    var newTaskMinutes by remember { mutableIntStateOf(25) }
    var setImmediatelyAsMission by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("task_engine_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TASK ENGINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = AtlasAmber,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Execution Queue",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                }

                Row {
                    IconButton(
                        onClick = { isAddingTask = !isAddingTask },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isAddingTask) AtlasAmber else CarbonSurfaceElevated)
                            .testTag("toggle_add_task_form_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Task",
                            tint = if (isAddingTask) CarbonSurface else TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CarbonSurfaceElevated)
                            .testTag("close_task_engine_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Add Task Form Dropdown
            AnimatedVisibility(visible = isAddingTask) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CarbonSurface)
                        .border(1.dp, CarbonBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "NEW EXECUTION TARGET",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = { Text("Task objective...", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AtlasAmber,
                            unfocusedBorderColor = CarbonBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AtlasAmber
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_task_title")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newTaskSteps,
                        onValueChange = { newTaskSteps = it },
                        placeholder = { Text("Micro-steps (one per line, e.g. Open editor)...", color = TextMuted) },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AtlasAmber,
                            unfocusedBorderColor = CarbonBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AtlasAmber
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_task_steps")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Duration picker
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            listOf(15, 25, 45).forEach { mins ->
                                Box(
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (newTaskMinutes == mins) AtlasAmber else CarbonSurfaceElevated)
                                        .clickable { newTaskMinutes = mins }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (newTaskMinutes == mins) CarbonSurface else TextSecondary
                                    )
                                }
                            }
                        }

                        // Submit
                        Button(
                            onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    val stepsList = newTaskSteps.lines().filter { it.isNotBlank() }
                                    onCreateTask(
                                        newTaskTitle,
                                        newTaskDescription,
                                        2,
                                        newTaskMinutes,
                                        stepsList,
                                        setImmediatelyAsMission
                                    )
                                    newTaskTitle = ""
                                    newTaskDescription = ""
                                    newTaskSteps = ""
                                    isAddingTask = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AtlasAmber,
                                contentColor = CarbonSurface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("submit_create_task_button")
                        ) {
                            Text("Save Target", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tabs: Active vs Completed
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = ObsidianBackground,
                contentColor = AtlasAmber,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AtlasAmber
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Active (${activeTasks.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selectedTab == 0) TextPrimary else TextMuted
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Done (${completedTasks.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selectedTab == 1) TextPrimary else TextMuted
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val tasksToShow = if (selectedTab == 0) activeTasks else completedTasks

            if (tasksToShow.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = if (selectedTab == 0) "No active targets. Take a moment to define one." else "No completed tasks yet today.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("task_engine_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasksToShow, key = { it.id }) { task ->
                        TaskItemRow(
                            task = task,
                            isMission = task.id == activeMissionId,
                            onToggleCompletion = { onToggleCompletion(task) },
                            onSetMission = { onSetMission(task.id) },
                            onDelete = { onDeleteTask(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItemRow(
    task: TaskEntity,
    isMission: Boolean,
    onToggleCompletion: () -> Unit,
    onSetMission: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isMission) CarbonSurfaceElevated else CarbonSurface)
            .border(
                1.dp,
                if (isMission) AtlasAmber else CarbonBorder,
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
            .testTag("task_item_${task.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Complete Checkbox
        IconButton(
            onClick = onToggleCompletion,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = "Toggle done",
                tint = if (task.isCompleted) AtlasAmber else TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isMission) {
                    Text(
                        text = "MISSION • ",
                        style = MaterialTheme.typography.labelSmall,
                        color = AtlasAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${task.estimatedMinutes}m",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (task.isCompleted) TextMuted else TextPrimary,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
            )

            val microSteps = task.getMicroSteps()
            if (microSteps.isNotEmpty()) {
                val doneCount = microSteps.count { it.second }
                Text(
                    text = "$doneCount/${microSteps.size} micro-steps",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        // Action: Promote to Mission
        if (!task.isCompleted && !isMission) {
            IconButton(
                onClick = onSetMission,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.StarBorder,
                    contentDescription = "Make Mission",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else if (isMission) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Active Mission",
                tint = AtlasAmber,
                modifier = Modifier
                    .size(36.dp)
                    .padding(8.dp)
            )
        }

        // Delete Action
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
