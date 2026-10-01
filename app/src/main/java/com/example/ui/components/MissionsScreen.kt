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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.AtlasCardElevated
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.CrimsonExecuteButtonBrush
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassCardBackgroundBrush
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.util.CalendarDay
import com.example.util.DateUtils
import java.util.Calendar

@Composable
fun MissionsScreen(
    allTasks: List<TaskEntity>,
    selectedCalendarDate: Long,
    activeMissionId: Long?,
    onSelectDate: (Long) -> Unit,
    onClose: () -> Unit,
    onSetMission: (Long) -> Unit,
    onToggleTaskCompletion: (TaskEntity) -> Unit,
    onCreateTask: (title: String, description: String, priority: Int, estimatedMinutes: Int, microSteps: List<String>, scheduledDate: Long, asMission: Boolean) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Current viewed calendar month/year
    val currentCal = remember { Calendar.getInstance() }
    var displayedYear by remember { mutableIntStateOf(currentCal.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableIntStateOf(currentCal.get(Calendar.MONTH)) }

    var showCreateDialog by remember { mutableStateOf(false) }

    // Selected day tasks
    val dateTasks = allTasks.filter { task ->
        DateUtils.isSameDay(task.scheduledDate, selectedCalendarDate)
    }.sortedWith(
        compareBy<TaskEntity> { it.isCompleted }
            .thenByDescending { it.isMission }
            .thenByDescending { it.priority }
            .thenByDescending { it.createdAt }
    )

    val monthDays = remember(displayedYear, displayedMonth) {
        DateUtils.getMonthDays(displayedYear, displayedMonth)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("missions_screen")
    ) {
        MountainMistBackdrop(alpha = 0.85f)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header: Back Arrow, Title & "+ Task" Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GlassCardBackgroundBrush)
                            .border(1.dp, GlassBorderBrush, CircleShape)
                            .testTag("close_missions_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Task & Calendar Planner",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp
                            ),
                            color = TextPureWhite
                        )
                        Text(
                            text = "Schedule tasks for any day",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CrimsonElectric)
                            .testTag("add_mission_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Assign Task",
                            tint = TextPureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Interactive Calendar Container
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(GlassCardBackgroundBrush)
                        .border(1.dp, GlassBorderBrush, RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        // Month & Year Navigator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = CrimsonElectric,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = DateUtils.formatMonthYear(displayedYear, displayedMonth),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    ),
                                    color = TextPureWhite
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Jump to Today chip
                                Text(
                                    text = "Today",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CrimsonElectric,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            val todayCal = Calendar.getInstance()
                                            displayedYear = todayCal.get(Calendar.YEAR)
                                            displayedMonth = todayCal.get(Calendar.MONTH)
                                            onSelectDate(DateUtils.getStartOfDay())
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                // Previous Month
                                IconButton(
                                    onClick = {
                                        if (displayedMonth == 0) {
                                            displayedMonth = 11
                                            displayedYear -= 1
                                        } else {
                                            displayedMonth -= 1
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Previous Month",
                                        tint = TextPureWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Next Month
                                IconButton(
                                    onClick = {
                                        if (displayedMonth == 11) {
                                            displayedMonth = 0
                                            displayedYear += 1
                                        } else {
                                            displayedMonth += 1
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Next Month",
                                        tint = TextPureWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Weekday Headers (Mon, Tue, Wed, Thu, Fri, Sat, Sun)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
                            daysOfWeek.forEach { dayName ->
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = TextMuted,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar Day Cells Grid
                        val chunkedDays = monthDays.chunked(7)
                        chunkedDays.forEach { week ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                week.forEach { day ->
                                    val isSelected = DateUtils.isSameDay(day.timeMillis, selectedCalendarDate)
                                    val hasTasks = allTasks.any { task ->
                                        DateUtils.isSameDay(task.scheduledDate, day.timeMillis)
                                    }

                                    CalendarDayCell(
                                        day = day,
                                        isSelected = isSelected,
                                        hasTasks = hasTasks,
                                        onSelect = { onSelectDate(day.timeMillis) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Agenda Header for Selected Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = DateUtils.formatDateHeader(selectedCalendarDate),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = TextPureWhite
                        )
                        Text(
                            text = "${dateTasks.count { it.isCompleted }} of ${dateTasks.size} tasks completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // + Schedule Task for this Date
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AtlasCardElevated)
                            .border(1.dp, GlassBorderBrush, RoundedCornerShape(12.dp))
                            .clickable { showCreateDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = CrimsonElectric,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Task",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPureWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Tasks List for the Selected Date
            if (dateTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(GlassCardBackgroundBrush)
                            .border(1.dp, GlassBorderBrush, RoundedCornerShape(18.dp))
                            .clickable { showCreateDialog = true }
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No tasks assigned for this date",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPureWhite,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap here to plan ahead for ${DateUtils.formatShortMonthDay(selectedCalendarDate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(dateTasks, key = { it.id }) { task ->
                    PlannerTaskCard(
                        task = task,
                        isMission = task.id == activeMissionId,
                        onToggle = { onToggleTaskCompletion(task) },
                        onSetMission = { onSetMission(task.id) },
                        onDelete = { onDeleteTask(task) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Schedule Task Dialog
        if (showCreateDialog) {
            ScheduleTaskDialog(
                initialDate = selectedCalendarDate,
                onDismiss = { showCreateDialog = false },
                onConfirm = { title, desc, duration, priority, scheduledDate, asMission ->
                    onCreateTask(title, desc, priority, duration, emptyList(), scheduledDate, asMission)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    hasTasks: Boolean,
    onSelect: () -> Unit
) {
    val cellShape = RoundedCornerShape(10.dp)

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(cellShape)
            .background(
                when {
                    isSelected -> CrimsonElectric
                    else -> Color.Transparent
                }
            )
            .border(
                1.dp,
                when {
                    isSelected -> Color.White
                    day.isToday -> CrimsonElectric.copy(alpha = 0.8f)
                    else -> Color.Transparent
                },
                cellShape
            )
            .clickable { onSelect() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${day.dayOfMonth}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                ),
                color = when {
                    isSelected -> TextPureWhite
                    !day.isCurrentMonth -> TextMuted.copy(alpha = 0.4f)
                    day.isToday -> CrimsonElectric
                    else -> TextPureWhite
                }
            )

            // Dot for scheduled tasks
            if (hasTasks) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else CrimsonElectric)
                )
            }
        }
    }
}

@Composable
private fun PlannerTaskCard(
    task: TaskEntity,
    isMission: Boolean,
    onToggle: () -> Unit,
    onSetMission: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassCardBackgroundBrush)
            .border(
                1.dp,
                if (isMission) SolidColor(CrimsonElectric.copy(alpha = 0.6f)) else GlassBorderBrush,
                RoundedCornerShape(16.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox with Crimson Accent
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = if (task.isCompleted) "Done" else "Incomplete",
                tint = if (task.isCompleted) CrimsonElectric else TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Description
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isMission) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = if (task.isCompleted) TextMuted else TextPureWhite,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Duration Badge
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

        Spacer(modifier = Modifier.width(6.dp))

        // Star as Mission
        IconButton(
            onClick = onSetMission,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = if (isMission) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = "Set Mission",
                tint = if (isMission) CrimsonElectric else TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }

        // Delete
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete",
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ScheduleTaskDialog(
    initialDate: Long,
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, duration: Int, priority: Int, scheduledDate: Long, asMission: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableIntStateOf(25) }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var setAsMission by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(AtlasBackground)
                .border(1.dp, GlassBorderBrush, RoundedCornerShape(26.dp))
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedule Task",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPureWhite
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Name", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonElectric,
                        unfocusedBorderColor = AtlasBorder,
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite,
                        cursorColor = CrimsonElectric
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes (optional)", color = TextSecondary) },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonElectric,
                        unfocusedBorderColor = AtlasBorder,
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite,
                        cursorColor = CrimsonElectric
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Date Label & Quick Chips
                Text(
                    text = "Scheduled For: ${DateUtils.formatDateHeader(selectedDate)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = CrimsonElectric
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val today = DateUtils.getStartOfDay()
                    val tomorrow = today + 24 * 60 * 60 * 1000L
                    val nextWeek = today + 7 * 24 * 60 * 60 * 1000L

                    listOf(
                        "Today" to today,
                        "Tomorrow" to tomorrow,
                        "+7 Days" to nextWeek
                    ).forEach { (label, dateMillis) ->
                        val isChosen = DateUtils.isSameDay(selectedDate, dateMillis)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isChosen) CrimsonElectric else AtlasCardElevated)
                                .clickable { selectedDate = dateMillis }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isChosen) TextPureWhite else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Duration Selector
                Text(
                    text = "Estimated Duration",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(15, 25, 45, 60).forEach { mins ->
                        val isSelected = selectedDuration == mins
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CrimsonElectric else AtlasCardElevated)
                                .clickable { selectedDuration = mins }
                                .padding(horizontal = 11.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${mins}m",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) TextPureWhite else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Checkbox: Set as Primary Mission
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { setAsMission = !setAsMission }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = setAsMission,
                        onCheckedChange = { setAsMission = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CrimsonElectric,
                            uncheckedColor = TextSecondary,
                            checkmarkColor = TextPureWhite
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Set as Primary Focus Mission",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPureWhite
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Schedule Button
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onConfirm(title, description, selectedDuration, 2, selectedDate, setAsMission)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonElectric),
                    shape = RoundedCornerShape(14.dp),
                    enabled = title.isNotBlank()
                ) {
                    Text(
                        text = "Save & Schedule",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPureWhite
                    )
                }
            }
        }
    }
}
