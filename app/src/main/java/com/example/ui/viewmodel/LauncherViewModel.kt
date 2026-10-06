package com.example.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import com.example.ui.appearance.*
import com.example.util.AtlasWallpaperSynchronizer
import com.example.util.WallpaperTarget
import com.example.util.ProfilePhotoProcessor
import com.example.util.GeminiClient
import com.example.util.WeatherService
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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
            wallpaperTarget = loadSavedWallpaperTarget(),
            lockWallpaper = loadSavedLockWallpaper(),
            lockCustomWallpaperPath = prefs?.getString("lock_custom_wallpaper_path", null),
            customAvatarPath = loadSavedCustomAvatarPath(),
            identityTitle = loadSavedIdentityTitle(),
            identityVocation = loadSavedIdentityVocation(),
            isOnboardingOpen = !loadOnboardingCompleted(),
            userTotalXp = loadSavedTotalXp(),
            claimedTodayXp = hasClaimedToday(),
            momentumDays = computeMomentum(loadSavedActiveDays()).coerceAtLeast(1),
            activeDayKeys = loadSavedActiveDays(),
            unlockedBadges = loadUnlockedBadges(),
            coachTone = loadSavedCoachTone(),
            defaultFocusPreset = loadSavedDefaultFocusPreset(),
            appearanceState = loadSavedAppearance()
        )
    )
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        observeData()
        checkInitialSeed()
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

    private fun loadSavedWallpaperTarget(): WallpaperTarget {
        val saved = prefs?.getString("wallpaper_target", null) ?: return WallpaperTarget.BOTH
        return try {
            WallpaperTarget.valueOf(saved)
        } catch (e: Exception) {
            WallpaperTarget.BOTH
        }
    }

    private fun loadSavedLockWallpaper(): BuiltInWallpaper? {
        val saved = prefs?.getString("lock_wallpaper_theme", null) ?: return null
        return try {
            BuiltInWallpaper.valueOf(saved)
        } catch (e: Exception) {
            null
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

    // ------------------------------------------------------------------
    // Real progression: daily claim, momentum streak & badge unlocks
    // ------------------------------------------------------------------

    private fun dayKey(timeInMillis: Long = System.currentTimeMillis()): String =
        SimpleDateFormat("yyyyMMdd", Locale.US).format(Date(timeInMillis))

    private fun hasClaimedToday(): Boolean =
        prefs?.getString("last_xp_claim_date", null) == dayKey()

    private fun loadSavedActiveDays(): Set<String> =
        prefs?.getString("active_days", "")
            ?.split(',')
            ?.mapNotNull { it.trim().takeIf { token -> token.isNotEmpty() } }
            ?.toSet()
            ?: emptySet()

    /**
     * Consecutive days with at least one real action (task completed, focus logged,
     * daily XP claimed), counted back from today (or yesterday when today is still open).
     */
    private fun computeMomentum(days: Set<String>): Int {
        if (days.isEmpty()) return 0
        val format = SimpleDateFormat("yyyyMMdd", Locale.US)
        val cursor = Calendar.getInstance()
        if (!days.contains(format.format(cursor.time))) {
            cursor.add(Calendar.DAY_OF_YEAR, -1)
            if (!days.contains(format.format(cursor.time))) return 0
        }
        var streak = 0
        while (days.contains(format.format(cursor.time))) {
            streak++
            cursor.add(Calendar.DAY_OF_YEAR, -1)
        }
        return streak
    }

    private fun loadUnlockedBadges(): Set<String> =
        prefs?.getString("unlocked_badges", null)
            ?.split(',')
            ?.mapNotNull { it.trim().takeIf { token -> token.isNotEmpty() } }
            ?.toSet()
            ?.ifEmpty { setOf("first_step") }
            ?: setOf("first_step")

    private fun unlockBadge(id: String) {
        val current = _uiState.value.unlockedBadges
        if (id in current) return
        val updated = current + id
        _uiState.update { it.copy(unlockedBadges = updated) }
        prefs?.edit()?.putString("unlocked_badges", updated.joinToString(","))?.apply()
    }

    /** Awards XP for real actions and persists the new total. */
    private fun awardXp(amount: Int) {
        if (amount <= 0) return
        val newXp = _uiState.value.userTotalXp + amount
        _uiState.update { it.copy(userTotalXp = newXp) }
        prefs?.edit()?.putInt("user_total_xp", newXp)?.apply()
        if (newXp >= 500) unlockBadge("xp_500")
    }

    /** Marks today as an active day, recomputes the streak and unlocks streak badges. */
    private fun markActiveDay() {
        val days = loadSavedActiveDays().toMutableSet()
        days.add(dayKey())
        prefs?.edit()?.putString("active_days", days.joinToString(","))?.apply()
        val streak = computeMomentum(days).coerceAtLeast(1)
        _uiState.update { it.copy(momentumDays = streak, activeDayKeys = days) }
        if (streak >= 7) unlockBadge("streak_7")
        if (streak >= 30) unlockBadge("streak_30")
    }

    private fun loadSavedCoachTone(): CoachTone {
        val saved = prefs?.getString("coach_tone", null) ?: return CoachTone.BALANCED
        return try {
            CoachTone.valueOf(saved)
        } catch (e: Exception) {
            CoachTone.BALANCED
        }
    }

    private fun loadSavedDefaultFocusPreset(): FocusPreset {
        val saved = prefs?.getString("default_focus_preset", null) ?: return FocusPreset.POMODORO_25
        return try {
            FocusPreset.valueOf(saved)
        } catch (e: Exception) {
            FocusPreset.POMODORO_25
        }
    }

    /** Cycles the Gemini coaching tone (Gentle -> Balanced -> Direct). */
    fun cycleCoachTone() {
        val next = when (_uiState.value.coachTone) {
            CoachTone.GENTLE -> CoachTone.BALANCED
            CoachTone.BALANCED -> CoachTone.DIRECT
            CoachTone.DIRECT -> CoachTone.GENTLE
        }
        _uiState.update { it.copy(coachTone = next) }
        prefs?.edit()?.putString("coach_tone", next.name)?.apply()
    }

    /** Cycles the default focus sprint length used when entering Focus Mode. */
    fun cycleDefaultFocusPreset() {
        val entries = FocusPreset.entries
        val next = entries[(_uiState.value.defaultFocusPreset.ordinal + 1) % entries.size]
        _uiState.update { it.copy(defaultFocusPreset = next) }
        prefs?.edit()?.putString("default_focus_preset", next.name)?.apply()
    }

    /** Loads cached weather instantly, then refreshes it from the network. */
    private fun loadWeather() {
        val ctx = context ?: return
        val cached = WeatherService.loadCached(ctx)
        if (cached != null) {
            _uiState.update { it.copy(weather = cached) }
        }
        viewModelScope.launch {
            val fresh = WeatherService.fetch(ctx)
            if (fresh != null) {
                _uiState.update { it.copy(weather = fresh) }
            }
        }
    }

    /**
     * "Proactive Micro-Step Breakdown": when enabled, setting a task as the mission
     * asks Gemini for 3 concrete first steps if the task has none yet.
     */
    private fun maybeGenerateMissionSteps(taskId: Long) {
        if (!_uiState.value.aiNudgesEnabled) return
        if (!GeminiClient.isConfigured) return
        viewModelScope.launch {
            try {
                val task = taskRepository.getTask(taskId) ?: return@launch
                if (task.microStepsRaw.isNotBlank()) return@launch
                val steps = GeminiClient.suggestMicroSteps(task.title, task.description)
                steps.forEach { step -> taskRepository.addMicroStepById(taskId, step) }
            } catch (e: Exception) {
                // AI is best-effort: never block the mission flow on a network failure
            }
        }
    }

    private fun loadSavedAppearance(): AppearanceState {
        val p = prefs ?: return AppearanceState()
        val fontStr = p.getString("appearance_font", AppFontFamily.SPACE_GROTESK.name)
        val fontFamily = try { AppFontFamily.valueOf(fontStr ?: "") } catch (e: Exception) { AppFontFamily.SPACE_GROTESK }

        val themeStr = p.getString("appearance_theme_mode", ThemeMode.AMOLED_BLACK.name)
        val themeMode = try { ThemeMode.valueOf(themeStr ?: "") } catch (e: Exception) { ThemeMode.AMOLED_BLACK }

        val accentStr = p.getString("appearance_accent_preset", AccentColorPreset.RED.name)
        val accentPreset = try { AccentColorPreset.valueOf(accentStr ?: "") } catch (e: Exception) { AccentColorPreset.RED }
        val customAccentHex = p.getString("appearance_custom_accent_hex", "#FF2A4D") ?: "#FF2A4D"

        val iconStr = p.getString("appearance_icon_style", IconStylePreset.ROUNDED.name)
        val iconStyle = try { IconStylePreset.valueOf(iconStr ?: "") } catch (e: Exception) { IconStylePreset.ROUNDED }

        val animStr = p.getString("appearance_animation_preset", AnimationPreset.BALANCED.name)
        val animationPreset = try { AnimationPreset.valueOf(animStr ?: "") } catch (e: Exception) { AnimationPreset.BALANCED }

        val cornerFactor = p.getFloat("appearance_corner_radius", 0.50f)
        val densityStr = p.getString("appearance_density", InterfaceDensity.COMFORTABLE.name)
        val density = try { InterfaceDensity.valueOf(densityStr ?: "") } catch (e: Exception) { InterfaceDensity.COMFORTABLE }

        val blurPercent = p.getInt("appearance_wallpaper_blur", 30)
        val cardTrans = p.getInt("appearance_card_transparency", 15)
        val dockTrans = p.getInt("appearance_dock_transparency", 20)
        val widgetTrans = p.getInt("appearance_widget_transparency", 10)
        val notesTrans = p.getInt("appearance_notes_transparency", 15)
        val searchTrans = p.getInt("appearance_search_transparency", 20)

        val widgetStyleStr = p.getString("appearance_widget_style", WidgetStyle.GLASS.name)
        val widgetStyle = try { WidgetStyle.valueOf(widgetStyleStr ?: "") } catch (e: Exception) { WidgetStyle.GLASS }

        val widgetCorner = p.getInt("appearance_widget_corner_radius", 22)
        val clockShowWeather = p.getInt("appearance_clock_show_weather", 1) == 1

        val statusStr = p.getString("appearance_status_bar", StatusBarMode.DEFAULT.name)
        val statusBarMode = try { StatusBarMode.valueOf(statusStr ?: "") } catch (e: Exception) { StatusBarMode.DEFAULT }

        val notesPaperStr = p.getString("appearance_notes_paper", NotesPaperStyle.PLAIN.name)
        val notesPaperStyle = try { NotesPaperStyle.valueOf(notesPaperStr ?: "") } catch (e: Exception) { NotesPaperStyle.PLAIN }

        val xpStyleStr = p.getString("appearance_xp_style", XpSystemStyle.GRADIENT_GLOW.name)
        val xpSystemStyle = try { XpSystemStyle.valueOf(xpStyleStr ?: "") } catch (e: Exception) { XpSystemStyle.GRADIENT_GLOW }

        val wpStyleStr = p.getString("appearance_wallpaper_style", WallpaperStylePreset.GRADIENT.name)
        val wallpaperStyle = try { WallpaperStylePreset.valueOf(wpStyleStr ?: "") } catch (e: Exception) { WallpaperStylePreset.GRADIENT }

        val clkStr = p.getString("clock_style_preset", ClockStylePreset.MODERN.name)
        val clockStylePreset = try { ClockStylePreset.valueOf(clkStr ?: "") } catch (e: Exception) { ClockStylePreset.MODERN }

        return AppearanceState(
            fontFamily = fontFamily,
            themeMode = themeMode,
            accentPreset = accentPreset,
            customAccentHex = customAccentHex,
            iconStyle = iconStyle,
            animationPreset = animationPreset,
            reduceMotion = p.getBoolean("appearance_reduce_motion", false),
            enablePhysics = p.getBoolean("appearance_enable_physics", true),
            enableBounce = p.getBoolean("appearance_enable_bounce", true),
            enableBlur = p.getBoolean("appearance_enable_blur", true),
            enableGlow = p.getBoolean("appearance_enable_glow", true),
            cornerRadiusFactor = cornerFactor,
            density = density,
            wallpaperBlurPercent = blurPercent,
            cardTransparency = cardTrans,
            dockTransparency = dockTrans,
            widgetTransparency = widgetTrans,
            notesTransparency = notesTrans,
            searchTransparency = searchTrans,
            wallpaperStyle = wallpaperStyle,
            clockStylePreset = clockStylePreset,
            widgetStyle = widgetStyle,
            widgetCornerRadiusDp = widgetCorner,
            clockShowWeather = clockShowWeather,
            statusBarMode = statusBarMode,
            notesPaperStyle = notesPaperStyle,
            xpSystemStyle = xpSystemStyle
        )
    }

    private fun saveAppearanceState(state: AppearanceState) {
        prefs?.edit()
            ?.putString("appearance_font", state.fontFamily.name)
            ?.putString("appearance_theme_mode", state.themeMode.name)
            ?.putString("appearance_accent_preset", state.accentPreset.name)
            ?.putString("appearance_custom_accent_hex", state.customAccentHex)
            ?.putString("appearance_icon_style", state.iconStyle.name)
            ?.putString("appearance_animation_preset", state.animationPreset.name)
            ?.putBoolean("appearance_reduce_motion", state.reduceMotion)
            ?.putBoolean("appearance_enable_physics", state.enablePhysics)
            ?.putBoolean("appearance_enable_bounce", state.enableBounce)
            ?.putBoolean("appearance_enable_blur", state.enableBlur)
            ?.putBoolean("appearance_enable_glow", state.enableGlow)
            ?.putFloat("appearance_corner_radius", state.cornerRadiusFactor)
            ?.putString("appearance_density", state.density.name)
            ?.putInt("appearance_wallpaper_blur", state.wallpaperBlurPercent)
            ?.putInt("appearance_card_transparency", state.cardTransparency)
            ?.putInt("appearance_dock_transparency", state.dockTransparency)
            ?.putInt("appearance_widget_transparency", state.widgetTransparency)
            ?.putInt("appearance_notes_transparency", state.notesTransparency)
            ?.putInt("appearance_search_transparency", state.searchTransparency)
            ?.putString("appearance_wallpaper_style", state.wallpaperStyle.name)
            ?.putString("clock_style_preset", state.clockStylePreset.name)
            ?.putString("appearance_widget_style", state.widgetStyle.name)
            ?.putInt("appearance_widget_corner_radius", state.widgetCornerRadiusDp)
            ?.putInt("appearance_clock_show_weather", if (state.clockShowWeather) 1 else 0)
            ?.putString("appearance_status_bar", state.statusBarMode.name)
            ?.putString("appearance_notes_paper", state.notesPaperStyle.name)
            ?.putString("appearance_xp_style", state.xpSystemStyle.name)
            ?.apply()
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

        // Collect apps stream. The dock list follows the persisted pin order
        // (never a re-sorted copy of the alphabetical app list) and has no
        // "random defaults" fallback, so pins stay exactly as the user left them.
        viewModelScope.launch {
            combine(
                appLauncherRepository.combinedApps,
                appLauncherRepository.pinnedApps
            ) { apps, pins ->
                apps to LauncherUiState.orderPinnedApps(apps, pins.map { it.packageName })
            }.collect { (apps, pinned) ->
                _uiState.update { current ->
                    val filtered = LauncherUiState.computeFilteredApps(
                        apps,
                        current.selectedAppCategory,
                        current.appSearchQuery
                    )
                    current.copy(
                        allApps = apps,
                        pinnedApps = pinned,
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
            val s = _uiState.value
            AtlasWallpaperSynchronizer.syncWallpaper(
                context = ctx,
                wallpaper = s.selectedWallpaper,
                customImagePath = s.customWallpaperPath,
                target = s.wallpaperTarget,
                lockWallpaper = s.lockWallpaper,
                lockCustomImagePath = s.lockCustomWallpaperPath
            )
        }

        // Real weather for the clock header (cached + refreshed on launch)
        loadWeather()
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

    /**
     * Dock management. Pin/move/remove are all explicit, ordered writes into the
     * persisted pinned table — the dock only changes when the user changes it.
     */
    fun pinAppToDock(app: AppItem, index: Int? = null) {
        viewModelScope.launch {
            appLauncherRepository.pinAppAt(app, index)
        }
    }

    fun moveDockApp(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            appLauncherRepository.movePin(fromIndex, toIndex)
        }
    }

    fun removeDockApp(app: AppItem) {
        viewModelScope.launch {
            appLauncherRepository.removePin(app)
        }
    }

    // App drawer context-menu actions
    fun saveAppCustomization(
        app: AppItem,
        customLabel: String?,
        iconPresetId: Int?,
        customIconUri: String?
    ) {
        viewModelScope.launch {
            appLauncherRepository.saveAppCustomization(app, customLabel, iconPresetId, customIconUri)
        }
    }

    fun resetAppCustomization(app: AppItem) {
        viewModelScope.launch {
            appLauncherRepository.resetAppCustomization(app)
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

    fun openAppearanceStudio() {
        openEditMode(EditCategory.THEME)
    }

    fun closeAppearanceStudio() {
        closeEditMode()
    }

    fun openEditMode(category: EditCategory? = null) {
        _uiState.update {
            it.copy(
                isEditMode = true,
                isSettingsOpen = false,
                isAppearanceStudioOpen = false,
                // Long-press on the home screen opens the editor MENU (circle
                // dock). Only explicit entries (top-bar "+", appearance studio)
                // pass a category.
                activeEditCategory = category
            )
        }
    }

    fun closeEditMode() {
        _uiState.update {
            it.copy(
                isEditMode = false,
                activeEditCategory = null
            )
        }
    }

    fun setEditCategory(category: EditCategory?) {
        _uiState.update {
            it.copy(activeEditCategory = category)
        }
    }

    fun updateAppearance(transform: (AppearanceState) -> AppearanceState) {
        val current = _uiState.value.appearanceState
        val updated = transform(current)
        _uiState.update { it.copy(appearanceState = updated) }
        saveAppearanceState(updated)
    }

    /** Restores every style control to the factory default look. */
    fun resetAppearanceToDefaults() {
        val defaults = AppearanceState()
        _uiState.update {
            it.copy(
                appearanceState = defaults,
                wallpaperDim = 0.35f,
                selectedWallpaper = BuiltInWallpaper.OBSIDIAN_VOID,
                wallpaperTarget = WallpaperTarget.BOTH,
                customWallpaperPath = null,
                lockWallpaper = null,
                lockCustomWallpaperPath = null
            )
        }
        saveAppearanceState(defaults)
        prefs?.edit()
            ?.putString("wallpaper_theme", BuiltInWallpaper.OBSIDIAN_VOID.name)
            ?.putFloat("wallpaper_dim", 0.35f)
            ?.putString("wallpaper_target", WallpaperTarget.BOTH.name)
            ?.putString("lock_wallpaper_theme", null)
            ?.putString("lock_custom_wallpaper_path", null)
            ?.apply()
        syncWallpaperToSystem(force = true)
    }

    fun setFontFamily(font: AppFontFamily) {
        updateAppearance { it.copy(fontFamily = font) }
    }

    fun setThemeMode(mode: ThemeMode) {
        updateAppearance { it.copy(themeMode = mode) }
    }

    fun setAccentPreset(preset: AccentColorPreset) {
        updateAppearance { it.copy(accentPreset = preset) }
        // Keep matching palette in sync for legacy references
        val matchingPalette = when (preset) {
            AccentColorPreset.RED -> ColorPalette.OBSIDIAN_CRIMSON
            AccentColorPreset.GREEN -> ColorPalette.CYBER_EMERALD
            AccentColorPreset.BLUE -> ColorPalette.ELECTRIC_AZURE
            AccentColorPreset.ORANGE -> ColorPalette.AMBER_GOLD
            AccentColorPreset.PURPLE -> ColorPalette.NEON_VIOLET
            AccentColorPreset.WHITE -> ColorPalette.MINIMAL_MONOCHROME
            AccentColorPreset.GRAY -> ColorPalette.MINIMAL_MONOCHROME
            AccentColorPreset.CUSTOM -> ColorPalette.OBSIDIAN_CRIMSON
        }
        setColorPalette(matchingPalette)
    }

    fun setCustomAccentHex(hex: String) {
        val cleanHex = if (hex.startsWith("#")) hex else "#$hex"
        updateAppearance {
            it.copy(
                accentPreset = AccentColorPreset.CUSTOM,
                customAccentHex = cleanHex
            )
        }
    }

    fun setIconStyle(style: IconStylePreset) {
        updateAppearance { it.copy(iconStyle = style) }
    }

    fun setAnimationPreset(preset: AnimationPreset) {
        updateAppearance { it.copy(animationPreset = preset) }
    }

    fun toggleReduceMotion() {
        updateAppearance { it.copy(reduceMotion = !it.reduceMotion) }
    }

    fun toggleEnablePhysics() {
        updateAppearance { it.copy(enablePhysics = !it.enablePhysics) }
    }

    fun toggleEnableBounce() {
        updateAppearance { it.copy(enableBounce = !it.enableBounce) }
    }

    fun toggleEnableBlur() {
        updateAppearance { it.copy(enableBlur = !it.enableBlur) }
    }

    fun toggleEnableGlow() {
        updateAppearance { it.copy(enableGlow = !it.enableGlow) }
    }

    fun setCornerRadiusFactor(factor: Float) {
        val clamped = factor.coerceIn(0f, 1f)
        updateAppearance { it.copy(cornerRadiusFactor = clamped) }
    }

    fun setInterfaceDensity(density: InterfaceDensity) {
        updateAppearance { it.copy(density = density) }
    }

    fun setWallpaperBlurPercent(percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        updateAppearance { it.copy(wallpaperBlurPercent = clamped) }
    }

    fun setWidgetStyle(style: WidgetStyle) {
        updateAppearance { it.copy(widgetStyle = style) }
    }

    fun setStatusBarMode(mode: StatusBarMode) {
        updateAppearance { it.copy(statusBarMode = mode) }
    }

    fun setNotesPaperStyle(style: NotesPaperStyle) {
        updateAppearance { it.copy(notesPaperStyle = style) }
    }

    fun setXpSystemStyle(style: XpSystemStyle) {
        updateAppearance { it.copy(xpSystemStyle = style) }
    }

    fun setTransparencyValues(
        card: Int? = null,
        dock: Int? = null,
        widget: Int? = null,
        notes: Int? = null,
        search: Int? = null
    ) {
        updateAppearance {
            it.copy(
                cardTransparency = card ?: it.cardTransparency,
                dockTransparency = dock ?: it.dockTransparency,
                widgetTransparency = widget ?: it.widgetTransparency,
                notesTransparency = notes ?: it.notesTransparency,
                searchTransparency = search ?: it.searchTransparency
            )
        }
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
        maybeGenerateMissionSteps(taskId)
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        val completing = !task.isCompleted
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(task)
            if (completing) {
                awardXp(if (task.isMission) 100 else 20)
                markActiveDay()
                unlockBadge("first_mission")
                // End-of-day badge: every task scheduled for today is done
                val othersToday = _uiState.value.todayTasks.filter { it.id != task.id }
                if (othersToday.isNotEmpty() && othersToday.all { it.isCompleted }) {
                    unlockBadge("night_warrior")
                }
            }
        }
    }

    fun toggleMicroStep(task: TaskEntity, stepIndex: Int) {
        viewModelScope.launch {
            taskRepository.toggleMicroStep(task, stepIndex)
        }
    }

    fun addMicroStep(task: TaskEntity, stepText: String) {
        viewModelScope.launch {
            taskRepository.addMicroStepById(task.id, stepText)
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
        // Single source of truth: choosing a color palette also retints the global
        // accent so Settings, Dock, Search, Sheets, Switches… all recolor together.
        updateAppearance {
            it.copy(
                customAccentHex = palette.hexCode,
                accentPreset = AccentColorPreset.CUSTOM
            )
        }
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
                selectedColorPalette = matchingPalette,
                // "Same wallpaper for both" is the default mental model: picking a new
                // wallpaper keeps the lock screen in sync unless the user explicitly
                // set a different lock wallpaper in the editor.
                lockWallpaper = if (it.wallpaperTarget == WallpaperTarget.HOME) it.lockWallpaper else null,
                lockCustomWallpaperPath = if (it.wallpaperTarget == WallpaperTarget.HOME) it.lockCustomWallpaperPath else null
            )
        }
        prefs?.edit()
            ?.putString("wallpaper_theme", wallpaper.name)
            ?.putString("color_palette", matchingPalette.name)
            ?.apply()

        syncWallpaperToSystem()
    }

    /** Updates which system surfaces the active wallpaper is applied to. */
    fun setWallpaperTarget(target: WallpaperTarget) {
        _uiState.update { it.copy(wallpaperTarget = target) }
        prefs?.edit()?.putString("wallpaper_target", target.name)?.apply()
        syncWallpaperToSystem(force = true)
    }

    /**
     * Sets a dedicated lock screen wallpaper. Passing null reverts the lock screen
     * to mirroring the home wallpaper ("Same wallpaper").
     */
    fun setLockWallpaper(wallpaper: BuiltInWallpaper?, customPath: String? = null) {
        _uiState.update {
            it.copy(
                lockWallpaper = wallpaper,
                lockCustomWallpaperPath = if (wallpaper == BuiltInWallpaper.CUSTOM_GALLERY) customPath else null
            )
        }
        prefs?.edit()
            ?.putString("lock_wallpaper_theme", wallpaper?.name)
            ?.putString("lock_custom_wallpaper_path", if (wallpaper == BuiltInWallpaper.CUSTOM_GALLERY) customPath else null)
            ?.apply()
        syncWallpaperToSystem(force = true)
    }

    /** Single source of truth for pushing the current wallpaper state to Android. */
    private fun syncWallpaperToSystem(force: Boolean = false) {
        viewModelScope.launch {
            val ctx = context ?: return@launch
            val state = _uiState.value
            AtlasWallpaperSynchronizer.syncWallpaper(
                context = ctx,
                wallpaper = state.selectedWallpaper,
                customImagePath = state.customWallpaperPath,
                force = force,
                target = state.wallpaperTarget,
                lockWallpaper = state.lockWallpaper,
                lockCustomImagePath = state.lockCustomWallpaperPath
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
                if (_uiState.value.wallpaperTarget == WallpaperTarget.LOCK) {
                    // Applying a custom photo to the lock screen only
                    prefs?.edit()
                        ?.putString("lock_wallpaper_theme", BuiltInWallpaper.CUSTOM_GALLERY.name)
                        ?.putString("lock_custom_wallpaper_path", path)
                        ?.apply()
                    _uiState.update {
                        it.copy(
                            lockWallpaper = BuiltInWallpaper.CUSTOM_GALLERY,
                            lockCustomWallpaperPath = path
                        )
                    }
                } else {
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
                }

                // Sync custom image to Android System Wallpaper (home + lock)
                syncWallpaperToSystem(force = true)

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
        syncWallpaperToSystem(force = true)
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
            val id = taskRepository.createTask(
                title = title,
                description = description,
                priority = priority,
                estimatedMinutes = estimatedMinutes,
                microSteps = microSteps,
                scheduledDate = scheduledDate,
                asMission = asMission
            )
            if (asMission) {
                maybeGenerateMissionSteps(id)
            }
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
        val selectedPreset = preset ?: _uiState.value.defaultFocusPreset
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
            // Real rewards: 1 XP per focus minute + streak + dawn badge
            awardXp(actualDuration / 60)
            markActiveDay()
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 8) {
                unlockBadge("early_bird")
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
        prefs?.edit()?.putString("last_xp_claim_date", dayKey())?.apply()
        _uiState.update { it.copy(claimedTodayXp = true) }
        awardXp(amount)
        markActiveDay()
    }

    fun setCustomAvatarFromUri(uri: Uri, onComplete: (Boolean) -> Unit = {}) {
        setProfilePhotoFromUri(uri, onComplete)
    }

    fun setProfilePhotoFromUri(uri: Uri, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ctx = context ?: return@launch
                val path = ProfilePhotoProcessor.processAndSavePhoto(ctx, uri)
                prefs?.edit()?.putString("custom_avatar_path", path)?.apply()
                _uiState.update { it.copy(customAvatarPath = path) }
                launch(Dispatchers.Main) { onComplete(true) }
            } catch (e: Exception) {
                launch(Dispatchers.Main) { onComplete(false) }
            }
        }
    }

    fun setProfilePhotoFromBitmap(bitmap: Bitmap, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ctx = context ?: return@launch
                val path = ProfilePhotoProcessor.processAndSaveBitmap(ctx, bitmap)
                prefs?.edit()?.putString("custom_avatar_path", path)?.apply()
                _uiState.update { it.copy(customAvatarPath = path) }
                launch(Dispatchers.Main) { onComplete(true) }
            } catch (e: Exception) {
                launch(Dispatchers.Main) { onComplete(false) }
            }
        }
    }

    fun clearCustomAvatar() {
        removeProfilePhoto()
    }

    fun removeProfilePhoto() {
        val current = _uiState.value.customAvatarPath
        if (current != null) {
            try {
                File(current).delete()
            } catch (_: Exception) {}
        }
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
