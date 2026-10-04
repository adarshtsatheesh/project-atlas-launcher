package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TaskEntity
import com.example.domain.model.AppItem
import com.example.ui.viewmodel.LauncherUiState
import kotlin.math.min

// ==========================================================================
// SINGLE SOURCE OF TRUTH — the home screen canvas
// ==========================================================================

/**
 * The real home screen content: system status-bar spacing, clock header
 * (weather, quick actions, date, quote), the mission widget and the floating
 * dock. Rendered by the actual [HomeScreen] and reused verbatim inside
 * [LauncherPreview] so the customization preview can never drift from reality.
 *
 * All styling is read from the live [LauncherUiState] (and the
 * `LocalAppearanceState` consumed by the child components), so any setting
 * change recomposes both the home screen and every preview at once.
 */
@Composable
fun LauncherHomeCanvas(
    uiState: LauncherUiState,
    isEditMode: Boolean,
    onToggleClockStyle: () -> Unit = {},
    onOpenGestureOverview: () -> Unit = {},
    onLockScreen: () -> Unit = {},
    onOpenAmbientLock: () -> Unit = {},
    onOpenProgressStats: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onStartFocus: () -> Unit = {},
    onToggleTask: (TaskEntity) -> Unit = {},
    onSetAsMission: (Long) -> Unit = {},
    onQuickAddTask: (String) -> Unit = {},
    onOpenCalendarPlanner: () -> Unit = {},
    onLaunchApp: (AppItem) -> Unit = {},
    onTogglePin: (AppItem) -> Unit = {},
    onOpenAppDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val appearance = uiState.appearanceState

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (!appearance.layoutHideStatusBar) Modifier.statusBarsPadding()
                else Modifier.padding(top = 10.dp)
            )
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // High-End Multi-Style Clock (Neo-Glass, Stacked Digital, Chronograph)
            AtlasClockHeader(
                clockStyle = uiState.selectedClockStyle,
                weather = uiState.weather,
                onToggleClockStyle = onToggleClockStyle,
                onOpenGestureOverview = onOpenGestureOverview,
                onLockScreen = onLockScreen,
                onOpenAmbientLock = onOpenAmbientLock,
                onOpenProgressStats = onOpenProgressStats,
                onOpenSettings = onOpenSettings
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Unified Minimal Glassmorphism Today's Tasks & Mission Console
            TodayMissionTaskConsole(
                activeMission = uiState.activeMission,
                tasks = uiState.todayTasks,
                isEditMode = isEditMode,
                onStartFocus = onStartFocus,
                onToggleTask = onToggleTask,
                onSetAsMission = onSetAsMission,
                onQuickAddTask = onQuickAddTask,
                onOpenCalendarPlanner = onOpenCalendarPlanner,
                modifier = Modifier.padding(
                    horizontal = (appearance.layoutWidgetPaddingDp - 8).coerceAtLeast(0).dp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Dock: essential apps & swipe up guide
        if (!appearance.layoutHideDock || isEditMode) {
            QuickDock(
                pinnedApps = uiState.pinnedApps,
                iconSize = uiState.appIconSize,
                hideLabels = uiState.hideDockLabels,
                isEditMode = isEditMode,
                onLaunchApp = onLaunchApp,
                onTogglePin = onTogglePin,
                onOpenAppDrawer = onOpenAppDrawer
            )
        }
    }
}

// ==========================================================================
// REUSABLE PHONE-MOCKUP LIVE PREVIEW
// ==========================================================================

/**
 * Premium phone mockup rendering the ENTIRE home screen — wallpaper, status
 * bar, clock, date, quote, widget, task cards and dock — inside a thin-bezel
 * AMOLED frame with the device's true aspect ratio.
 *
 * The content is the real [LauncherHomeCanvas] rendered full-size and scaled
 * down via [graphicsLayer] (vector-accurate, no bitmaps, no screenshots), so
 * it stays live-synchronized with every setting. Interaction callbacks are
 * no-ops: the preview is a display, not a remote control.
 */
@Composable
fun LauncherPreview(
    uiState: LauncherUiState,
    modifier: Modifier = Modifier
) {
    val appearance = uiState.appearanceState
    val configuration = LocalConfiguration.current
    val screenW = configuration.screenWidthDp.dp
    val screenH = configuration.screenHeightDp.dp
    val realDensity = LocalDensity.current
    val statusInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val bezel = 7.dp
    val frameRadius = 34.dp
    val screenRadius = 27.dp

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Fit the phone (frame included) inside the pane, keeping the real aspect
        val scale = min(
            (maxWidth.value - bezel.value * 2).coerceAtLeast(1f) / screenW.value,
            (maxHeight.value - bezel.value * 2).coerceAtLeast(1f) / screenH.value
        )
        val innerW = screenW * scale
        val innerH = screenH * scale

        // Thin-bezel AMOLED phone frame
        Box(
            modifier = Modifier
                .size(width = innerW + bezel * 2, height = innerH + bezel * 2)
                .clip(RoundedCornerShape(frameRadius))
                .background(Color(0xFF04050A))
                .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(frameRadius))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bezel)
                    .clip(RoundedCornerShape(screenRadius))
            ) {
                // Live wallpaper — the exact backdrop composable used by home
                AtlasWallpaperBackdrop(
                    wallpaper = uiState.selectedWallpaper,
                    customImagePath = uiState.customWallpaperPath,
                    dimAlpha = uiState.wallpaperDim
                )

                // Scale every dp of the REAL home canvas by `scale` through a
                // local density — true proportions, crisp vectors, no oversized
                // layout and no bitmap rendering.
                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = realDensity.density * scale,
                        fontScale = realDensity.fontScale
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            // Insets are re-applied pre-scaled below, so stop them
                            // leaking into the canvas (statusBarsPadding, dock
                            // navigationBarsPadding would otherwise double up).
                            .consumeWindowInsets(WindowInsets.systemBars)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = if (!appearance.layoutHideStatusBar) statusInset
                                    else 0.dp
                                )
                        ) {
                            LauncherHomeCanvas(
                                uiState = uiState,
                                isEditMode = false
                            )
                        }

                        // Decorative status bar so the mockup reads as a real device
                        if (!appearance.layoutHideStatusBar) {
                            PreviewStatusBar(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .fillMaxWidth()
                                    .height(statusInset)
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Decorative status bar drawn inside the preview's scaled canvas. */
@Composable
private fun PreviewStatusBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 22.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "12:30",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp
            ),
            color = Color.White
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 13.dp, height = 7.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.85f))
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f))
            )
        }
    }
}
