package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.viewmodel.AvatarStyle

/**
 * Psychology-first personal evolution and identity screen.
 * Seamlessly forwards to [JourneyScreen] to eliminate the legacy analytics dashboard.
 */
@Composable
fun ProgressStatsScreen(
    todayFocusMinutes: Int,
    completedTasksCount: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    JourneyScreen(
        profileName = "Alex",
        activeMission = null,
        completedTasksCount = completedTasksCount,
        totalFocusMinutes = todayFocusMinutes,
        notesCount = 4,
        userTotalXp = 0,
        avatarStyle = AvatarStyle.MINIMAL_HOODIE,
        momentumDays = 1,
        onContinueMission = { onClose() },
        onClose = onClose,
        modifier = modifier
    )
}
