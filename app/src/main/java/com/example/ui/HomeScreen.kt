package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.appearance.LiveEditorBottomDock
import com.example.ui.appearance.LiveEditorTopBar
import com.example.ui.components.AiAssistantScreen
import com.example.ui.components.AmbientLockScreen
import com.example.ui.components.AppDrawerSheet
import com.example.ui.components.FocusModeOverlay
import com.example.ui.components.GestureOverviewSheet
import com.example.ui.components.InfiniteNotesScreen
import com.example.ui.components.JourneyScreen
import com.example.ui.components.LauncherHomeCanvas
import com.example.ui.components.MissionsScreen
import com.example.ui.components.MountainMistBackdrop
import com.example.ui.components.AtlasWallpaperBackdrop
import com.example.ui.components.OnboardingSheet
import com.example.ui.components.SettingsScreen
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.AtlasCardElevated
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LauncherViewModel
import com.example.ui.viewmodel.MockupScreen
import com.example.util.LockScreenManager
import kotlin.math.abs

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var showLockPermissionDialog by remember { mutableStateOf(false) }

    // Direct system lock trigger for real device lock screen
    val lockDeviceAction: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        val locked = LockScreenManager.lockDevice(context)
        if (!locked) {
            showLockPermissionDialog = true
        }
    }

    // Handle Back Press for any open full-screen modals or overlays
    val anyModalOpen = uiState.isEditMode ||
            uiState.isAppDrawerOpen ||
            uiState.isInfiniteNotesOpen ||
            uiState.isFutureDashboardOpen ||
            uiState.isGestureOverviewOpen ||
            uiState.isTaskEngineOpen ||
            uiState.isFocusModeActive ||
            uiState.isMissionsOpen ||
            uiState.isProgressOpen ||
            uiState.isAiCoachOpen ||
            uiState.isSettingsOpen ||
            uiState.isAppearanceStudioOpen ||
            uiState.isOnboardingOpen

    BackHandler(enabled = anyModalOpen) {
        when {
            uiState.isEditMode -> {
                if (uiState.activeEditCategory != null) {
                    viewModel.setEditCategory(null)
                } else {
                    viewModel.closeEditMode()
                }
            }
            uiState.isAppearanceStudioOpen -> viewModel.closeAppearanceStudio()
            uiState.isFocusModeActive -> viewModel.finishFocusSession(completedSuccessfully = false)
            uiState.isInfiniteNotesOpen -> viewModel.closeInfiniteNotes()
            uiState.isFutureDashboardOpen -> viewModel.closeFutureDashboard()
            uiState.isGestureOverviewOpen -> viewModel.closeGestureOverview()
            uiState.isAppDrawerOpen -> viewModel.closeAppDrawer()
            uiState.isAiCoachOpen -> viewModel.closeAiCoach()
            uiState.isProgressOpen -> viewModel.closeProgress()
            uiState.isSettingsOpen -> viewModel.closeSettings()
            uiState.isMissionsOpen -> viewModel.closeMissions()
            uiState.isTaskEngineOpen -> viewModel.closeTaskEngine()
            uiState.isOnboardingOpen -> viewModel.closeOnboarding()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasBackground)
            .pointerInput(uiState.isEditMode) {
                detectTapGestures(
                    onTap = {
                        if (uiState.isEditMode && uiState.activeEditCategory != null) {
                            viewModel.setEditCategory(null)
                        }
                    },
                    onLongPress = {
                        viewModel.openEditMode()
                    }
                )
            }
            .pointerInput(uiState.isEditMode) {
                if (uiState.isEditMode) return@pointerInput
                var totalDragX = 0f
                var totalDragY = 0f
                var gestureFired = false

                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                        gestureFired = false
                    },
                    onDrag = { change, dragAmount ->
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y

                        if (!gestureFired) {
                            val dragThresholdY = -35f // Swiping up
                            val dragThresholdX = 60f

                            if (abs(totalDragY) > abs(totalDragX) && totalDragY < dragThresholdY) {
                                // Instantly trigger App Drawer on swipe up from ANYWHERE on home screen!
                                gestureFired = true
                                change.consume()
                                viewModel.openAppDrawer()
                            } else if (abs(totalDragX) > abs(totalDragY) && abs(totalDragX) > dragThresholdX) {
                                // Horizontal swipe: Left swipe opens Journey, Right swipe opens Notes
                                gestureFired = true
                                change.consume()
                                if (totalDragX < 0) {
                                    // Swiping leftward (Swipe Left) -> Infinite Notes!
                                    viewModel.openInfiniteNotes(page = 0)
                                } else {
                                    // Swiping rightward (Swipe Right) -> Journey screen!
                                    viewModel.openFutureDashboard()
                                }
                            }
                        }
                    },
                    onDragEnd = {
                        gestureFired = false
                        totalDragX = 0f
                        totalDragY = 0f
                    },
                    onDragCancel = {
                        gestureFired = false
                        totalDragX = 0f
                        totalDragY = 0f
                    }
                )
            }
            .testTag("home_screen_container")
    ) {
        // Atmospheric Glassmorphic Radiant Backdrop
        val editModeDarken by animateFloatAsState(
            targetValue = if (uiState.isEditMode) 0.12f else 0f,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
            label = "edit_darken"
        )
        val editModeBlur by animateDpAsState(
            targetValue = if (uiState.isEditMode) 8.dp else 0.dp,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
            label = "edit_blur"
        )
        val homeContentScale by animateFloatAsState(
            targetValue = if (uiState.isEditMode) (if (uiState.activeEditCategory != null) 0.90f else 0.95f) else 1.0f,
            animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
            label = "home_content_scale"
        )
        val homeContentTranslationY by animateDpAsState(
            targetValue = if (uiState.isEditMode && uiState.activeEditCategory != null) (-42).dp else 0.dp,
            animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
            label = "home_content_trans_y"
        )

        AtlasWallpaperBackdrop(
            wallpaper = uiState.selectedWallpaper,
            customImagePath = uiState.customWallpaperPath,
            dimAlpha = uiState.wallpaperDim + editModeDarken,
            modifier = if (editModeBlur > 0.dp) Modifier.blur(editModeBlur) else Modifier
        )

        // Empty-area catcher (bottom layer): double-tap-to-lock and long-press
        // editor entry ONLY fire on bare home screen regions (Nova/OneUI/ColorOS
        // behavior). The clock, widget, icons and dock render ABOVE this and
        // consume their own taps, so they never trigger either gesture.
        if (!uiState.isEditMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { lockDeviceAction() },
                            onLongPress = { viewModel.openEditMode() }
                        )
                    }
            )
        }

        if (uiState.isEditMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.12f))
            )
        }

        // Main Calm Launcher Home View — the shared [LauncherHomeCanvas] keeps
        // the real home screen and every editor preview pixel-identical.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = homeContentScale
                    scaleY = homeContentScale
                    translationY = homeContentTranslationY.toPx()
                }
        ) {
            LauncherHomeCanvas(
                uiState = uiState,
                isEditMode = uiState.isEditMode,
                onToggleClockStyle = {
                    if (uiState.isEditMode) {
                        viewModel.setEditCategory(com.example.ui.viewmodel.EditCategory.CLOCK)
                    } else {
                        viewModel.toggleClockStyle()
                    }
                },
                onOpenGestureOverview = { if (!uiState.isEditMode) viewModel.openGestureOverview() },
                onLockScreen = { if (!uiState.isEditMode) lockDeviceAction() },
                onOpenAmbientLock = { if (!uiState.isEditMode) viewModel.openAmbientLock() },
                onOpenProgressStats = { if (!uiState.isEditMode) viewModel.openProgress() },
                onOpenSettings = { if (!uiState.isEditMode) viewModel.openSettings() },
                onStartFocus = { if (!uiState.isEditMode) viewModel.startFocusMode() },
                onToggleTask = { viewModel.toggleTaskCompletion(it) },
                onSetAsMission = { viewModel.setActiveMission(it) },
                onQuickAddTask = { title ->
                    viewModel.createTask(
                        title = title,
                        estimatedMinutes = 25,
                        priority = 2,
                        scheduledDate = System.currentTimeMillis()
                    )
                },
                onOpenCalendarPlanner = { if (!uiState.isEditMode) viewModel.openMissions() },
                onLaunchApp = { if (!uiState.isEditMode) viewModel.launchApp(it) },
                onTogglePin = { viewModel.toggleAppPin(it) },
                onOpenAppDrawer = { if (!uiState.isEditMode) viewModel.openAppDrawer() }
            )
        }

        // Top Navigation Bar for Live Home Screen Editor
        AnimatedVisibility(
            visible = uiState.isEditMode,
            enter = fadeIn(tween(220)) + slideInVertically { -it },
            exit = fadeOut(tween(180)) + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            LiveEditorTopBar(
                onAdd = { viewModel.setEditCategory(com.example.ui.viewmodel.EditCategory.WALLPAPER) },
                onDone = { viewModel.closeEditMode() }
            )
        }

        // Floating Bottom Customization Dock for Live Home Screen Editor
        AnimatedVisibility(
            visible = uiState.isEditMode,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)
            ) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            LiveEditorBottomDock(
                activeCategory = uiState.activeEditCategory,
                onSelectCategory = { viewModel.setEditCategory(it) },
                onDone = { viewModel.closeEditMode() },
                uiState = uiState,
                onUpdateAppearance = { viewModel.updateAppearance(it) },
                onSelectWallpaperTarget = { viewModel.setWallpaperTarget(it) },
                onUpdateWallpaperDim = { viewModel.setWallpaperDim(it) },
                onSelectClockStyle = { viewModel.setClockStyle(it) },
                onSelectIconSize = { viewModel.setAppIconSize(it) },
                onToggleHideAppLabels = { viewModel.setHideAppLabels(it) },
                onToggleHideDockLabels = { viewModel.setHideDockLabels(it) },
                onSelectGridColumns = { viewModel.setDrawerGridColumns(it) },
                onSelectWallpaper = { viewModel.setWallpaper(it) },
                onSetCustomWallpaper = { viewModel.setCustomWallpaperFromUri(android.net.Uri.parse(it)) },
                onResetAppearance = { viewModel.resetAppearanceToDefaults() }
            )
        }

        // Layer: Infinite Notes Screen (Slide left brings in notes from the right)
        AnimatedVisibility(
            visible = uiState.isInfiniteNotesOpen,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            InfiniteNotesScreen(
                notes = uiState.notes,
                initialPage = uiState.infiniteNotesInitialPage,
                onSaveNote = { pageIndex, title, content ->
                    viewModel.saveNote(pageIndex, title, content)
                },
                onNewPage = { callback ->
                    viewModel.createNewNotePage(callback)
                },
                onClose = { viewModel.closeInfiniteNotes() }
            )
        }

        // Layer: App Drawer Sheet (Swipe Up)
        AnimatedVisibility(
            visible = uiState.isAppDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            AppDrawerSheet(
                apps = uiState.filteredApps,
                searchQuery = uiState.appSearchQuery,
                selectedCategory = uiState.selectedAppCategory,
                iconSize = uiState.appIconSize,
                hideAppLabels = uiState.hideAppLabels,
                drawerGridColumns = uiState.drawerGridColumns,
                wallpaper = uiState.selectedWallpaper,
                customWallpaperPath = uiState.customWallpaperPath,
                onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                onSelectCategory = { viewModel.selectAppCategory(it) },
                onLaunchApp = { viewModel.launchApp(it) },
                onTogglePin = { viewModel.toggleAppPin(it) },
                onClose = { viewModel.closeAppDrawer() }
            )
        }

        // Layer: Journey Screen (Left Swipe Destination)
        AnimatedVisibility(
            visible = uiState.isFutureDashboardOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            JourneyScreen(
                profileName = uiState.profileName,
                activeMission = uiState.activeMission,
                completedTasksCount = uiState.todayCompletedCount,
                totalFocusMinutes = uiState.todayFocusSeconds / 60,
                notesCount = uiState.notes.size,
                userTotalXp = uiState.userTotalXp,
                claimedTodayXp = uiState.claimedTodayXp,
                customAvatarPath = uiState.customAvatarPath,
                identityTitle = uiState.identityTitle,
                identityVocation = uiState.identityVocation,
                currentFormTitle = uiState.currentFormTitle,
                nextEvolutionTitle = uiState.nextEvolutionTitle,
                momentumDays = uiState.momentumDays,
                unlockedBadges = uiState.unlockedBadges,
                colorPalette = uiState.selectedColorPalette,
                onClaimXp = { viewModel.claimTodayXp(it) },
                onUpdateIdentityTitle = { viewModel.setIdentityTitle(it) },
                onUpdateProfileName = { viewModel.setProfileName(it) },
                onUploadCustomAvatar = { viewModel.setCustomAvatarFromUri(it) },
                onCaptureCustomAvatarBitmap = { viewModel.setProfilePhotoFromBitmap(it) },
                onClearCustomAvatar = { viewModel.removeProfilePhoto() },
                onContinueMission = { mission ->
                    viewModel.closeFutureDashboard()
                    if (mission != null) {
                        viewModel.startFocusMode()
                    } else {
                        viewModel.openMissions()
                    }
                },
                onClose = { viewModel.closeFutureDashboard() }
            )
        }

        // Layer: Gesture Overview Sheet
        AnimatedVisibility(
            visible = uiState.isGestureOverviewOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            GestureOverviewSheet(
                onSelectMockup = { viewModel.showMockupScreen(it) },
                onClose = { viewModel.closeGestureOverview() }
            )
        }

        // Layer: Missions Management Screen
        AnimatedVisibility(
            visible = uiState.isMissionsOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            MissionsScreen(
                allTasks = uiState.activeTasks + uiState.completedTasks,
                selectedCalendarDate = uiState.selectedCalendarDate,
                activeMissionId = uiState.activeMission?.id,
                onSelectDate = { viewModel.selectCalendarDate(it) },
                onClose = { viewModel.closeMissions() },
                onSetMission = { viewModel.setActiveMission(it) },
                onToggleTaskCompletion = { viewModel.toggleTaskCompletion(it) },
                onCreateTask = { title, desc, prio, mins, microSteps, scheduledDate, asMission ->
                    viewModel.createTask(title, desc, prio, mins, microSteps, scheduledDate, asMission)
                },
                onDeleteTask = { viewModel.deleteTask(it) }
            )
        }

        // Layer: Personal Evolution & Identity Screen (Stats / Progress Shortcut)
        AnimatedVisibility(
            visible = uiState.isProgressOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            JourneyScreen(
                profileName = uiState.profileName,
                activeMission = uiState.activeMission,
                completedTasksCount = uiState.todayCompletedCount,
                totalFocusMinutes = uiState.todayFocusSeconds / 60,
                notesCount = uiState.notes.size,
                userTotalXp = uiState.userTotalXp,
                claimedTodayXp = uiState.claimedTodayXp,
                customAvatarPath = uiState.customAvatarPath,
                identityTitle = uiState.identityTitle,
                identityVocation = uiState.identityVocation,
                currentFormTitle = uiState.currentFormTitle,
                nextEvolutionTitle = uiState.nextEvolutionTitle,
                momentumDays = uiState.momentumDays,
                unlockedBadges = uiState.unlockedBadges,
                colorPalette = uiState.selectedColorPalette,
                onClaimXp = { viewModel.claimTodayXp(it) },
                onUpdateIdentityTitle = { viewModel.setIdentityTitle(it) },
                onUpdateProfileName = { viewModel.setProfileName(it) },
                onUploadCustomAvatar = { viewModel.setCustomAvatarFromUri(it) },
                onCaptureCustomAvatarBitmap = { viewModel.setProfilePhotoFromBitmap(it) },
                onClearCustomAvatar = { viewModel.removeProfilePhoto() },
                onContinueMission = { mission ->
                    viewModel.closeProgress()
                    if (mission != null) {
                        viewModel.startFocusMode()
                    } else {
                        viewModel.openMissions()
                    }
                },
                onClose = { viewModel.closeProgress() }
            )
        }

        // Layer: Invisible AI Assistant
        AnimatedVisibility(
            visible = uiState.isAiCoachOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            AiAssistantScreen(
                activeMission = uiState.activeMission,
                coachContext = uiState.coachContext,
                onClose = { viewModel.closeAiCoach() },
                onAddMicroStepToMission = { task, step ->
                    viewModel.addMicroStep(task, step)
                }
            )
        }

        // Layer: Settings Screen (Long Press gesture destination)
        AnimatedVisibility(
            visible = uiState.isSettingsOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            SettingsScreen(
                profileName = uiState.profileName,
                colorPalette = uiState.selectedColorPalette,
                aiNudgesEnabled = uiState.aiNudgesEnabled,
                ambientSoundEnabled = uiState.ambientSoundEnabled,
                exitFrictionEnabled = uiState.exitFrictionEnabled,
                profilePhotoPath = uiState.customAvatarPath,
                onUploadProfilePhotoUri = { viewModel.setCustomAvatarFromUri(it) },
                onRemoveProfilePhoto = { viewModel.removeProfilePhoto() },
                onUpdateProfileName = { viewModel.setProfileName(it) },
                onSelectColorPalette = { viewModel.setColorPalette(it) },
                onToggleAiNudges = { viewModel.setAiNudgesEnabled(it) },
                onToggleAmbientSound = { viewModel.setAmbientSoundEnabled(it) },
                onToggleExitFriction = { viewModel.setExitFrictionEnabled(it) },
                onOpenAiCoach = { viewModel.openAiCoach() },
                coachTone = uiState.coachTone,
                defaultFocusPreset = uiState.defaultFocusPreset,
                onCycleCoachTone = { viewModel.cycleCoachTone() },
                onCycleDefaultFocusPreset = { viewModel.cycleDefaultFocusPreset() },
                onOpenAppearanceStudio = { viewModel.openAppearanceStudio() },
                onClose = { viewModel.closeSettings() }
            )
        }

        // Layer: Ambient Lock Screen (Minimal aesthetic lock display)
        AnimatedVisibility(
            visible = uiState.isAmbientLockOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AmbientLockScreen(
                activeMission = uiState.activeMission,
                profileName = uiState.profileName,
                onUnlock = { viewModel.closeAmbientLock() },
                onExecuteMission = {
                    viewModel.closeAmbientLock()
                    viewModel.startFocusMode()
                }
            )
        }

        // Permission Setup Dialog for Screen Lock
        if (showLockPermissionDialog) {
            LockPermissionDialog(
                onOpenSettings = {
                    showLockPermissionDialog = false
                    LockScreenManager.openAccessibilitySettings(context)
                },
                onDismiss = { showLockPermissionDialog = false }
            )
        }

        // Layer: Onboarding Sheet
        AnimatedVisibility(
            visible = uiState.isOnboardingOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                OnboardingSheet(
                    currentTitle = uiState.identityTitle,
                    customProfilePhotoPath = uiState.customAvatarPath,
                    onSelectTitle = { viewModel.setIdentityTitle(it) },
                    onUploadProfilePhotoUri = { viewModel.setCustomAvatarFromUri(it) },
                    onCaptureProfilePhotoBitmap = { viewModel.setProfilePhotoFromBitmap(it) },
                    onClearProfilePhoto = { viewModel.removeProfilePhoto() },
                    onCompleteOnboarding = { name, vocation ->
                        viewModel.completeOnboarding(name, vocation)
                    },
                    onDismiss = { viewModel.closeOnboarding() },
                    onSelectIntention = { viewModel.setUserIntention(it) }
                )
            }
        }

        // Layer: Immersive Focus Mode (Breathing animation & Timer)
        AnimatedVisibility(
            visible = uiState.isFocusModeActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FocusModeOverlay(
                activeMission = uiState.activeMission,
                timerSecondsRemaining = uiState.focusTimerSecondsRemaining,
                initialTimerSeconds = uiState.focusTimerInitialSeconds,
                isTimerRunning = uiState.isFocusTimerRunning,
                selectedPreset = uiState.selectedFocusModePreset,
                isBreathingActive = uiState.isBreathingPulseActive,
                ambientSoundEnabled = uiState.ambientSoundEnabled,
                exitFrictionEnabled = uiState.exitFrictionEnabled,
                onPause = { viewModel.pauseFocusTimer() },
                onResume = { viewModel.resumeFocusTimer() },
                onSelectPreset = { viewModel.selectFocusPreset(it) },
                onToggleBreathing = { viewModel.toggleBreathingPulse() },
                onToggleMicroStep = { mission, index ->
                    viewModel.toggleMicroStep(mission, index)
                },
                onFinishSession = { completed ->
                    viewModel.finishFocusSession(completed)
                }
            )
        }
    }
}

@Composable
private fun LockPermissionDialog(
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xF5101218))
                .border(1.dp, GlassBorderBrush, RoundedCornerShape(26.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon with glowing neon crimson aura
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(com.example.ui.theme.LocalAtlasAccent.current.copy(alpha = 0.16f))
                        .border(1.dp, com.example.ui.theme.LocalAtlasAccent.current.copy(alpha = 0.5f), RoundedCornerShape(27.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = com.example.ui.theme.LocalAtlasAccent.current,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Enable Screen Lock",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = TextPureWhite,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "To turn off and lock the mobile screen on double-tap or via the lock button, Android requires the Atlas Accessibility Service.\n\nTap below to open Accessibility Settings, select Atlas, and turn it on.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Primary Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CrimsonElectric)
                        .clickable { onOpenSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Open Accessibility Settings",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = TextPureWhite
                    )
                }

                // Dismiss Button
                Text(
                    text = "Not Now",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    color = TextMuted,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDismiss() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
