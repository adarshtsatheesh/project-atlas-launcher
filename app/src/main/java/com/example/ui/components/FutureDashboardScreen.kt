package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Backward compatibility wrapper forwarding to [JourneyScreen].
 */
@Composable
fun FutureDashboardScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    JourneyScreen(
        profileName = "Alex",
        activeMission = null,
        completedTasksCount = 14,
        totalFocusMinutes = 75,
        notesCount = 6,
        onContinueMission = { onClose() },
        onClose = onClose,
        modifier = modifier
    )
}
