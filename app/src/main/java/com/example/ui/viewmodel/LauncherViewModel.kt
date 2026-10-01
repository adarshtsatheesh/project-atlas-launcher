package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.avatar.AvatarConfig
import com.example.avatar.AvatarEngine
import com.example.avatar.AvatarStorage
import com.example.data.local.entity.TaskEntity
import com.example.data.repository.AppLauncherRepository
import com.example.data.repository.FocusRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.TaskRepository
import com.example.domain.model.AppCategory
import com.example.domain.model.AppIconSize
import com.example.domain.model.AppItem
import com.example.domain.model.BuiltInWallpaper
import com.example.domain.model.ColorPalette
import com.example.util.AtlasWallpaperSynchronizer
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LauncherViewModel(
    private val taskRepository: TaskRepository,
    private val focusRepository: FocusRepository,
    private val appLauncherRepository: AppLauncherRepository,
    private val noteRepository: NoteRepository,
    private val context: Context? = null
) : ViewModel() {

    private val prefs by lazy {
        context?.applicationContext?.getSharedPreferences("atlas_launcher_prefs", Context.MODE_PRIVATE)
    }

    private val avatarEngine by lazy {
        context?.let { AvatarEngine(it) }
    }

    private val _uiState = MutableStateFlow(
        LauncherUiState(
            selectedClockStyle = loadSavedClockStyle(),
            profileName = loadSavedProfileName(),
            selectedColorPalette = loadSavedColorPalette(),
            appIconSize = loadSavedIconSize(),
            hideAppLabels = loadSavedHideAppLabels(),
            hideDockLabels = loadSavedHideDockLabels(),
            drawerGridColumns = loadSavedDrawerGridColumns(),
            aiNudgesEnabled = loadSavedAiNudges(),
            ambientSoundEnabled = loadSavedAmbientSound(),
            exitFrictionEnabled = loadSavedExitFriction(),
            selectedWallpaper = loadSavedWallpaper(),
            customWallpaperPath = loadSavedCustomWallpaperPath(),
            wallpaperDim = loadSavedWallpaperDim(),
            avatarConfig = loadInitialAvatarConfig(),
            avatarStyle = loadSavedAvatarStyle(),
            customAvatarPath = loadSavedCustomAvatarPath(),
            identityTitle = loadSavedIdentityTitle(),
            identityVocation = loadSavedIdentityVocation(),
            isOnboardingOpen = !loadOnboardingCompleted(),
            userTotalXp = loadSavedTotalXp()
        )
    )
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        observeData()
        checkInitialSeed()
        observeAvatar()
    }

    private fun loadInitialAvatarConfig(): AvatarConfig {
        return context?.let { AvatarStorage(it).loadAvatar() } ?: AvatarConfig.DEFAULT_MASCULINE
    }

    private fun observeAvatar() {
        avatarEngine?.let { engine ->
            viewModelScope.launch {
                engine.currentAvatar.collect { cfg ->
                    _uiState.update { it.copy(avatarConfig = cfg) }
                }
            }
        }
    }

    private fun loadSavedClockStyle(): ClockStyle {
        val saved = prefs?.getString("clock_style", null) ?: return ClockStyle.NEO_GLASS
        return try {
            ClockStyle.valueOf(saved)
        } catch (e: Exception) {
            ClockStyle.NEO_GLASS
        }
    }

    private fun loadSavedProfileName(): String {
        return prefs?.getString("profile_name", "Alex") ?: "Alex"
    }

    private fun loadSavedColorPalette(): ColorPalette {
        val saved = prefs?.getString("color_palette", null) ?: return ColorPalette.OBSIDIAN_CRIMSON
        return try {
            ColorPalette.valueOf(saved)
        } catch (e: Exception) {
            ColorPalette.OBSIDIAN_CRIMSON
        }
    }

    private fun loadSavedIconSize(): AppIconSize {
        val saved = prefs?.getString("app_icon_size", null) ?: return AppIconSize.DEFAULT
        return try {
            AppIconSize.valueOf(saved)
        } catch (e: Exception) {
            AppIconSize.DEFAULT
        }
    }

    private fun loadSavedHideAppLabels(): Boolean {
        return prefs?.getBoolean("hide_app_labels", false) ?: false
    }

    private fun loadSavedHideDockLabels(): Boolean {
        return prefs?.getBoolean("hide_dock_labels", true) ?: true
    }

    private fun loadSavedDrawerGridColumns(): Int {
        return prefs?.getInt("drawer_grid_columns", 4) ?: 4
    }

    private fun loadSavedAiNudges(): Boolean {
        return prefs?.getBoolean("ai_nudges_enabled", true) ?: true
    }

    private fun loadSavedAmbientSound(): Boolean {
        return prefs?.getBoolean("ambient_sound_enabled", true) ?: true
    }

    private fun loadSavedExitFriction(): Boolean {
        return prefs?.getBoolean("exit_friction_enabled", true) ?: true
    }

    private fun loadSavedWallpaper(): BuiltInWallpaper {
        val saved = prefs?.getString("wallpaper_theme", null) ?: return BuiltInWallpaper.OBSIDIAN_VOID
        return try {
            BuiltInWallpaper.valueOf(saved)
        } catch (e: Exception) {
            BuiltInWallpaper.OBSIDIAN_VOID
        }
    }

    private fun loadSavedCustomWallpaperPath(): String? {
        val path = prefs?.getString("custom_wallpaper_path", null) ?: return null
        return if (File(path).exists()) path else null
    }

    private fun loadSavedWallpaperDim(): Float {
        return prefs?.getFloat("wallpaper_dim", 0.35f) ?: 0.35f
    }

    private fun loadSavedAvatarStyle(): AvatarStyle {
        val saved = prefs?.getString("avatar_style", null) ?: return AvatarStyle.MINIMAL_HOODIE
        return try {
            AvatarStyle.valueOf(saved)
        } catch (e: Exception) {
            AvatarStyle.MINIMAL_HOODIE
        }
    }

    private fun loadSavedCustomAvatarPath(): String? {
        val path = prefs?.getString("custom_avatar_path", null) ?: return null
        return if (File(path).exists()) path else null
    }

    private fun loadSavedIdentityTitle(): IdentityTitle {
        val saved = prefs?.getString("identity_title", null) ?: return IdentityTitle.THE_ARCHITECT
        return try {
            IdentityTitle.valueOf(saved)
        } catch (e: Exception) {
            IdentityTitle.THE_ARCHITECT
        }
    }

    private fun loadOnboardingCompleted(): Boolean {
        return prefs?.getBoolean("onboarding_completed", false) ?: false
    }

    private fun loadSavedIdentityVocation(): String {
        return prefs?.getString("identity_vocation", "Developer") ?: "Developer"
    }

    private fun loadSavedTotalXp(): Int {
        return prefs?.getInt("user_total_xp", 0) ?: 0
    }

    private fun checkInitialSeed() {
        viewModelScope.launch {
            val tasks = taskRepository.allTasks.first()
            if (tasks.isEmpty()) {
                taskRepository.seedInitialDataIfEmpty()
            }
            noteRepository.seedInitialNotesIfEmpty()
        }
    }

    private fun observeData() {
        // Collect task and focus streams
        viewModelScope.launch {
            combine(
                taskRepository.activeMission,
                taskRepository.activeTasks,
                taskRepository.completedTasks,
                focusRepository.todayFocusSeconds,
                taskRepository.todayCompletedCount
            ) { mission, active, completed, focusSecs, completedCount ->
                MissionBundle(mission, active, completed, focusSecs, completedCount)
            }.collect { bundle ->
                _uiState.update { current ->
                    val today = LauncherUiState.computeTodayTasks(bundle.active, bundle.completed)
                    val selected = LauncherUiState.computeSelectedDateTasks(bundle.active, bundle.completed, current.selectedCalendarDate)
                    current.copy(
                        activeMission = bundle.mission,
                        activeTasks = bundle.active,
                        completedTasks = bundle.completed,
                        todayTasks = today,
                        selectedDateTasks = selected,
                        todayFocusSeconds = bundle.focusSecs,
                        todayCompletedCount = bundle.completedCount
                    )
                }
            }
        }

        // Collect apps stream
        viewModelScope.launch {
            appLauncherRepository.combinedApps.collect { apps ->
                val pinned = apps.filter { it.isPinned }
                _uiState.update { current ->
                    val filtered = LauncherUiState.computeFilteredApps(
                        apps,
                        current.selectedAppCategory,
                        current.appSearchQuery
                    )
                    current.copy(
                        allApps = apps,
                        pinnedApps = if (pinned.isNotEmpty()) pinned else apps.take(4),
                        filteredApps = filtered
                    )
                }
            }
        }

        // Collect notes stream with distinct check to avoid redundant recompositions
        viewModelScope.launch {
            noteRepository.allNotes.collect { noteList ->
                _uiState.update { current ->
                    if (current.notes == noteList) current else current.copy(notes = noteList)
                }
            }
        }

        // Seamless System Recents & Wallpaper Synchronization
        viewModelScope.launch {
            val ctx = context ?: return@launch
            AtlasWallpaperSynchronizer.syncWallpaper(
                context = ctx,
                wallpaper = _uiState.value.selectedWallpaper,
                customImagePath = _uiState.value.customWallpaperPath
            )
        }
    }

    // Gesture & Screen Navigation Handlers
    fun openInfiniteNotes(page: Int = 0) {
        _uiState.update {
            it.copy(
                isInfiniteNotesOpen = true,
                infiniteNotesInitialPage = page,
                isAppDrawerOpen = false,
                isFutureDashboardOpen = false,
                isGestureOverviewOpen = false,
                currentMockupPreview = null
            )
        }
    }

    fun closeInfiniteNotes() {
        _uiState.update { it.copy(isInfiniteNotesOpen = false) }
    }

    fun openFutureDashboard() {
        _uiState.update {
            it.copy(
                isFutureDashboardOpen = true,
                isAppDrawerOpen = false,
                isInfiniteNotesOpen = false,
                isGestureOverviewOpen = false,
                currentMockupPreview = null
            )
        }
    }

    fun closeFutureDashboard() {
        _uiState.update { it.copy(isFutureDashboardOpen = false) }
    }

    fun openGestureOverview() {
        _uiState.update {
            it.copy(
                isGestureOverviewOpen = true,
                isAppDrawerOpen = false,
                isInfiniteNotesOpen = false,
                isFutureDashboardOpen = false
            )
        }
    }

    fun closeGestureOverview() {
        _uiState.update { it.copy(isGestureOverviewOpen = false) }
    }

    // Mockup Screen Direct Switcher (for prompt presentation output)
    fun showMockupScreen(screen: MockupScreen) {
        when (screen) {
            MockupScreen.HOME -> {
                _uiState.update {
                    it.copy(
                        isAppDrawerOpen = false,
                        isInfiniteNotesOpen = false,
                        isFutureDashboardOpen = false,
                        isGestureOverviewOpen = false,
                        isSettingsOpen = false,
                        currentMockupPreview = MockupScreen.HOME
                    )
                }
            }
            MockupScreen.NOTES_P1 -> {
                openInfiniteNotes(page = 0)
                _uiState.update { it.copy(currentMockupPreview = MockupScreen.NOTES_P1) }
            }
            MockupScreen.NOTES_P2 -> {
                openInfiniteNotes(page = 1)
                _uiState.update { it.copy(currentMockupPreview = MockupScreen.NOTES_P2) }
            }
            MockupScreen.APP_DRAWER -> {
                openAppDrawer()
                _uiState.update { it.copy(currentMockupPreview = MockupScreen.APP_DRAWER) }
            }
            MockupScreen.GESTURE_OVERVIEW -> {
                openGestureOverview()
                _uiState.update { it.copy(currentMockupPreview = MockupScreen.GESTURE_OVERVIEW) }
            }
            MockupScreen.AMBIENT_LOCK -> {
                openAmbientLock()
                _uiState.update { it.copy(currentMockupPreview = MockupScreen.AMBIENT_LOCK) }
            }
        }
    }

    // Note actions
    fun saveNote(pageIndex: Int, title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.saveNote(pageIndex, title, content)
        }
    }

    fun createNewNotePage(onCreated: (Int) -> Unit = {}) {
        viewModelScope.launch {
            val newIndex = noteRepository.createNewPage()
            onCreated(newIndex)
        }
    }

    // App Drawer Actions
    fun refreshApps() {
        viewModelScope.launch {
            appLauncherRepository.refreshApps()
        }
    }

    fun onPackageRemoved(packageName: String) {
        viewModelScope.launch {
            appLauncherRepository.onPackageRemoved(packageName)
        }
    }

    fun openAppDrawer() {
        if (_uiState.value.allApps.isEmpty()) {
            refreshApps()
        }
        _uiState.update {
            it.copy(
                isAppDrawerOpen = true,
                appSearchQuery = "",
                isInfiniteNotesOpen = false,
                isFutureDashboardOpen = false,
                isGestureOverviewOpen = false
            )
        }
    }

    fun closeAppDrawer() {
        _uiState.update { current ->
            val filtered = LauncherUiState.computeFilteredApps(current.allApps, current.selectedAppCategory, "")
            current.copy(isAppDrawerOpen = false, appSearchQuery = "", filteredApps = filtered)
        }
    }

    fun selectAppCategory(category: AppCategory) {
        _uiState.update { current ->
            val filtered = LauncherUiState.computeFilteredApps(current.allApps, category, current.appSearchQuery)
            current.copy(selectedAppCategory = category, filteredApps = filtered)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            val filtered = LauncherUiState.computeFilteredApps(current.allApps, current.selectedAppCategory, query)
            current.copy(appSearchQuery = query, filteredApps = filtered)
        }
    }

    fun launchApp(app: AppItem) {
        viewModelScope.launch {
            appLauncherRepository.launchApp(app)
            _uiState.update { it.copy(isAppDrawerOpen = false) }
        }
    }

    fun toggleAppPin(app: AppItem) {
        viewModelScope.launch {
            appLauncherRepository.togglePin(app)
        }
    }

    // Task & Mission Handlers
    fun openMissions() {
        _uiState.update { it.copy(isMissionsOpen = true) }
    }

    fun closeMissions() {
        _uiState.update { it.copy(isMissionsOpen = false) }
    }

    fun openProgress() {
        _uiState.update { it.copy(isProgressOpen = true) }
    }

    fun closeProgress() {
        _uiState.update { it.copy(isProgressOpen = false) }
    }

    fun openAiCoach() {
        _uiState.update { it.copy(isAiCoachOpen = true) }
    }

    fun closeAiCoach() {
        _uiState.update { it.copy(isAiCoachOpen = false) }
    }

    fun openSettings() {
        _uiState.update { it.copy(isSettingsOpen = true) }
    }

    fun closeSettings() {
        _uiState.update { it.copy(isSettingsOpen = false) }
    }

    fun openAmbientLock() {
        _uiState.update { it.copy(isAmbientLockOpen = true) }
    }

    fun closeAmbientLock() {
        _uiState.update { it.copy(isAmbientLockOpen = false) }
    }

    fun openOnboarding() {
        _uiState.update { it.copy(isOnboardingOpen = true) }
    }

    fun closeOnboarding() {
        _uiState.update { it.copy(isOnboardingOpen = false) }
    }

    fun setUserIntention(intention: String) {
        _uiState.update { it.copy(userIntention = intention) }
    }

    fun openTaskEngine() {
        _uiState.update { it.copy(isTaskEngineOpen = true) }
    }

    fun closeTaskEngine() {
        _uiState.update { it.copy(isTaskEngineOpen = false) }
    }

    fun setActiveMission(taskId: Long) {
        viewModelScope.launch {
            taskRepository.setMission(taskId)
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(task)
        }
    }

    fun toggleMicroStep(task: TaskEntity, stepIndex: Int) {
        viewModelScope.launch {
            taskRepository.toggleMicroStep(task, stepIndex)
        }
    }

    fun addMicroStep(task: TaskEntity, stepText: String) {
        viewModelScope.launch {
            taskRepository.addMicroStep(task, stepText)
        }
    }

    fun selectCalendarDate(dateMillis: Long) {
        _uiState.update { it.copy(selectedCalendarDate = dateMillis) }
    }

    fun setClockStyle(style: ClockStyle) {
        if (_uiState.value.selectedClockStyle == style) return
        _uiState.update { it.copy(selectedClockStyle = style) }
        prefs?.edit()?.putString("clock_style", style.name)?.apply()
    }

    fun toggleClockStyle() {
        val current = _uiState.value.selectedClockStyle
        val next = when (current) {
            ClockStyle.NEO_GLASS -> ClockStyle.STACKED_DIGITAL
            ClockStyle.STACKED_DIGITAL -> ClockStyle.CHRONOGRAPH
            ClockStyle.CHRONOGRAPH -> ClockStyle.NEO_GLASS
        }
        setClockStyle(next)
    }

    fun setProfileName(name: String) {
        val trimmed = name.trim().take(30)
        _uiState.update { it.copy(profileName = trimmed) }
        prefs?.edit()?.putString("profile_name", trimmed)?.apply()
    }

    fun setColorPalette(palette: ColorPalette) {
        _uiState.update { it.copy(selectedColorPalette = palette) }
        prefs?.edit()?.putString("color_palette", palette.name)?.apply()
    }

    fun setAppIconSize(size: AppIconSize) {
        _uiState.update { it.copy(appIconSize = size) }
        prefs?.edit()?.putString("app_icon_size", size.name)?.apply()
    }

    fun toggleHideAppLabels() {
        val newValue = !_uiState.value.hideAppLabels
        _uiState.update { it.copy(hideAppLabels = newValue) }
        prefs?.edit()?.putBoolean("hide_app_labels", newValue)?.apply()
    }

    fun setHideAppLabels(hide: Boolean) {
        _uiState.update { it.copy(hideAppLabels = hide) }
        prefs?.edit()?.putBoolean("hide_app_labels", hide)?.apply()
    }

    fun setHideDockLabels(hide: Boolean) {
        _uiState.update { it.copy(hideDockLabels = hide) }
        prefs?.edit()?.putBoolean("hide_dock_labels", hide)?.apply()
    }

    fun setDrawerGridColumns(columns: Int) {
        val safeCols = columns.coerceIn(3, 5)
        _uiState.update { it.copy(drawerGridColumns = safeCols) }
        prefs?.edit()?.putInt("drawer_grid_columns", safeCols)?.apply()
    }

    fun setAiNudgesEnabled(enabled: Boolean) {
        _uiState.update { it.copy(aiNudgesEnabled = enabled) }
        prefs?.edit()?.putBoolean("ai_nudges_enabled", enabled)?.apply()
    }

    fun setAmbientSoundEnabled(enabled: Boolean) {
        _uiState.update { it.copy(ambientSoundEnabled = enabled) }
        prefs?.edit()?.putBoolean("ambient_sound_enabled", enabled)?.apply()
    }

    fun setExitFrictionEnabled(enabled: Boolean) {
        _uiState.update { it.copy(exitFrictionEnabled = enabled) }
        prefs?.edit()?.putBoolean("exit_friction_enabled", enabled)?.apply()
    }

    fun setWallpaper(wallpaper: BuiltInWallpaper) {
        val matchingPalette = when (wallpaper) {
            BuiltInWallpaper.OBSIDIAN_VOID -> ColorPalette.OBSIDIAN_CRIMSON
            BuiltInWallpaper.CYBER_EMERALD -> ColorPalette.CYBER_EMERALD
            BuiltInWallpaper.ELECTRIC_AZURE -> ColorPalette.ELECTRIC_AZURE
            BuiltInWallpaper.AMBER_ECLIPSE -> ColorPalette.AMBER_GOLD
            BuiltInWallpaper.VIOLET_TWILIGHT -> ColorPalette.NEON_VIOLET
            BuiltInWallpaper.STEALTH_OLED -> ColorPalette.MINIMAL_MONOCHROME
            BuiltInWallpaper.CUSTOM_GALLERY -> _uiState.value.selectedColorPalette
        }
        _uiState.update {
            it.copy(
                selectedWallpaper = wallpaper,
                selectedColorPalette = matchingPalette
            )
        }
        prefs?.edit()
            ?.putString("wallpaper_theme", wallpaper.name)
            ?.putString("color_palette", matchingPalette.name)
            ?.apply()

        // Sync with Android System Wallpaper & Recents
        viewModelScope.launch {
            val ctx = context ?: return@launch
            AtlasWallpaperSynchronizer.syncWallpaper(
                context = ctx,
                wallpaper = wallpaper,
                customImagePath = null
            )
        }
    }

    fun setWallpaperDim(dim: Float) {
        val clamped = dim.coerceIn(0.1f, 0.85f)
        _uiState.update { it.copy(wallpaperDim = clamped) }
        prefs?.edit()?.putFloat("wallpaper_dim", clamped)?.apply()
    }

    fun setCustomWallpaperFromUri(uri: Uri, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ctx = context ?: return@launch
                val inputStream = ctx.contentResolver.openInputStream(uri) ?: return@launch
                // Clean up any old custom wallpapers to optimize storage
                ctx.filesDir.listFiles()?.forEach { file ->
                    if (file.name.startsWith("custom_wallpaper_") && file.name.endsWith(".jpg")) {
                        file.delete()
                    }
                }
                val wallpaperFile = File(ctx.filesDir, "custom_wallpaper_${System.currentTimeMillis()}.jpg")
                FileOutputStream(wallpaperFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                val path = wallpaperFile.absolutePath
                prefs?.edit()
                    ?.putString("custom_wallpaper_path", path)
                    ?.putString("wallpaper_theme", BuiltInWallpaper.CUSTOM_GALLERY.name)
                    ?.apply()

                _uiState.update {
                    it.copy(
                        selectedWallpaper = BuiltInWallpaper.CUSTOM_GALLERY,
                        customWallpaperPath = path
                    )
                }

                // Sync custom image to Android System Wallpaper
                launch {
                    AtlasWallpaperSynchronizer.syncWallpaper(
                        context = ctx,
                        wallpaper = BuiltInWallpaper.CUSTOM_GALLERY,
                        customImagePath = path,
                        force = true
                    )
                }

                launch(Dispatchers.Main) { onComplete(true) }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) { onComplete(false) }
            }
        }
    }

    fun clearCustomWallpaper() {
        prefs?.edit()
            ?.remove("custom_wallpaper_path")
            ?.putString("wallpaper_theme", BuiltInWallpaper.OBSIDIAN_VOID.name)
            ?.apply()
        _uiState.update {
            it.copy(
                selectedWallpaper = BuiltInWallpaper.OBSIDIAN_VOID,
                customWallpaperPath = null
            )
        }
        viewModelScope.launch {
            val ctx = context ?: return@launch
            AtlasWallpaperSynchronizer.syncWallpaper(
                context = ctx,
                wallpaper = BuiltInWallpaper.OBSIDIAN_VOID,
                customImagePath = null,
                force = true
            )
        }
    }

    fun createTask(
        title: String,
        description: String = "",
        priority: Int = 1,
        estimatedMinutes: Int = 25,
        microSteps: List<String> = emptyList(),
        scheduledDate: Long = _uiState.value.selectedCalendarDate,
        asMission: Boolean = false
    ) {
        viewModelScope.launch {
            taskRepository.createTask(
                title = title,
                description = description,
                priority = priority,
                estimatedMinutes = estimatedMinutes,
                microSteps = microSteps,
                scheduledDate = scheduledDate,
                asMission = asMission
            )
        }
    }

    fun rescheduleTask(task: TaskEntity, newDate: Long) {
        viewModelScope.launch {
            taskRepository.rescheduleTask(task, newDate)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    // Focus Mode Actions
    fun startFocusMode(preset: FocusPreset? = null) {
        val selectedPreset = preset ?: _uiState.value.selectedFocusModePreset
        val durationSeconds = selectedPreset.minutes * 60
        _uiState.update {
            it.copy(
                isFocusModeActive = true,
                selectedFocusModePreset = selectedPreset,
                focusTimerInitialSeconds = durationSeconds,
                focusTimerSecondsRemaining = durationSeconds,
                isFocusTimerRunning = true
            )
        }
        startTimerTicker()
    }

    fun pauseFocusTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(isFocusTimerRunning = false) }
    }

    fun resumeFocusTimer() {
        _uiState.update { it.copy(isFocusTimerRunning = true) }
        startTimerTicker()
    }

    fun toggleBreathingPulse() {
        _uiState.update { it.copy(isBreathingPulseActive = !it.isBreathingPulseActive) }
    }

    fun selectFocusPreset(preset: FocusPreset) {
        val durationSeconds = preset.minutes * 60
        _uiState.update {
            it.copy(
                selectedFocusModePreset = preset,
                focusTimerInitialSeconds = durationSeconds,
                focusTimerSecondsRemaining = durationSeconds,
                isFocusTimerRunning = false
            )
        }
        timerJob?.cancel()
        timerJob = null
    }

    fun finishFocusSession(completedSuccessfully: Boolean = true) {
        timerJob?.cancel()
        timerJob = null

        val current = _uiState.value
        val elapsedSeconds = current.focusTimerInitialSeconds - current.focusTimerSecondsRemaining
        val actualDuration = if (completedSuccessfully) current.focusTimerInitialSeconds else elapsedSeconds

        if (actualDuration >= 10) {
            viewModelScope.launch {
                val mission = current.activeMission
                focusRepository.recordSession(
                    taskId = mission?.id,
                    taskTitle = mission?.title ?: "Open Focus",
                    durationSeconds = actualDuration,
                    mode = current.selectedFocusModePreset.name,
                    completedSuccessfully = completedSuccessfully
                )
                if (mission != null) {
                    taskRepository.addFocusMinutes(mission.id, actualDuration / 60)
                }
            }
        }

        _uiState.update {
            it.copy(
                isFocusModeActive = false,
                isFocusTimerRunning = false
            )
        }
    }

    private fun startTimerTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val remaining = _uiState.value.focusTimerSecondsRemaining
                if (remaining > 1) {
                    _uiState.update { it.copy(focusTimerSecondsRemaining = remaining - 1) }
                } else {
                    _uiState.update { it.copy(focusTimerSecondsRemaining = 0, isFocusTimerRunning = false) }
                    finishFocusSession(completedSuccessfully = true)
                    break
                }
            }
        }
    }

    fun setAvatarStyle(style: AvatarStyle) {
        _uiState.update { it.copy(avatarStyle = style) }
        prefs?.edit()?.putString("avatar_style", style.name)?.apply()
    }

    fun setIdentityTitle(title: IdentityTitle) {
        _uiState.update { it.copy(identityTitle = title) }
        prefs?.edit()?.putString("identity_title", title.name)?.apply()
    }

    fun setIdentityVocation(vocation: String) {
        _uiState.update { it.copy(identityVocation = vocation) }
        prefs?.edit()?.putString("identity_vocation", vocation)?.apply()
    }

    fun completeOnboarding(name: String, vocation: String) {
        setProfileName(name)
        setIdentityVocation(vocation)
        prefs?.edit()?.putBoolean("onboarding_completed", true)?.apply()
        _uiState.update { it.copy(isOnboardingOpen = false) }
    }

    fun openLevelUpModal() {
        _uiState.update { it.copy(isLevelUpModalOpen = true) }
    }

    fun closeLevelUpModal() {
        _uiState.update { it.copy(isLevelUpModalOpen = false) }
    }

    fun claimTodayXp(amount: Int = 140) {
        if (_uiState.value.claimedTodayXp) return
        val newXp = _uiState.value.userTotalXp + amount
        _uiState.update {
            it.copy(
                userTotalXp = newXp,
                claimedTodayXp = true
            )
        }
        prefs?.edit()?.putInt("user_total_xp", newXp)?.apply()
    }

    fun setCustomAvatarFromUri(uri: Uri, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ctx = context ?: return@launch
                val inputStream = ctx.contentResolver.openInputStream(uri) ?: return@launch
                val avatarFile = File(ctx.filesDir, "custom_avatar_${System.currentTimeMillis()}.jpg")
                FileOutputStream(avatarFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                val path = avatarFile.absolutePath
                prefs?.edit()?.putString("custom_avatar_path", path)?.apply()
                _uiState.update { it.copy(customAvatarPath = path) }

                // Generate and cache stylized digital avatar from the uploaded photo
                avatarEngine?.let { engine ->
                    val newConfig = engine.generateFromUri(uri)
                    _uiState.update { it.copy(avatarConfig = newConfig) }
                }

                launch(Dispatchers.Main) { onComplete(true) }
            } catch (e: Exception) {
                launch(Dispatchers.Main) { onComplete(false) }
            }
        }
    }

    fun updateAvatarConfig(config: AvatarConfig) {
        avatarEngine?.updateConfig(config)
        _uiState.update { it.copy(avatarConfig = config) }
    }

    fun resetAvatar() {
        avatarEngine?.resetAvatar()
        val defaultCfg = AvatarConfig.DEFAULT_MASCULINE
        avatarEngine?.updateConfig(defaultCfg)
        _uiState.update { it.copy(avatarConfig = defaultCfg, customAvatarPath = null) }
        prefs?.edit()?.remove("custom_avatar_path")?.apply()
    }

    fun clearCustomAvatar() {
        prefs?.edit()?.remove("custom_avatar_path")?.apply()
        _uiState.update { it.copy(customAvatarPath = null) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    private data class MissionBundle(
        val mission: TaskEntity?,
        val active: List<TaskEntity>,
        val completed: List<TaskEntity>,
        val focusSecs: Int,
        val completedCount: Int
    )

    class Factory(
        private val taskRepository: TaskRepository,
        private val focusRepository: FocusRepository,
        private val appLauncherRepository: AppLauncherRepository,
        private val noteRepository: NoteRepository,
        private val context: Context? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LauncherViewModel::class.java)) {
                return LauncherViewModel(taskRepository, focusRepository, appLauncherRepository, noteRepository, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
