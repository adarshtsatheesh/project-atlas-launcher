package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.domain.model.ColorPalette

/**
 * Psychology-first personal evolution and identity screen.
 * Seamlessly forwards to [JourneyScreen] to eliminate the legacy analytics dashboard.
 */
@Composable
fun ProgressStatsScreen(
    todayFocusMinutes: Int,
    completedTasksCount: Int,
    colorPalette: ColorPalette = ColorPalette.OBSIDIAN_CRIMSON,
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
        momentumDays = 1,
        colorPalette = colorPalette,
        onContinueMission = { onClose() },
        onClose = onClose,
        modifier = modifier
    )
}
