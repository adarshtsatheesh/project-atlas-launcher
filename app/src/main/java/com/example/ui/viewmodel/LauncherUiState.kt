package com.example.ui.viewmodel

import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.TaskEntity
import com.example.domain.model.AppCategory
import com.example.domain.model.AppIconSize
import com.example.domain.model.AppItem
import com.example.domain.model.BuiltInWallpaper
import com.example.domain.model.ColorPalette
import com.example.ui.appearance.AppearanceState
import com.example.util.DateUtils

enum class ClockStyle(val label: String) {
    NEO_GLASS("Neo Glass"),
    STACKED_DIGITAL("Stacked Lockscreen"),
    CHRONOGRAPH("Glass Chrono")
}

enum class MockupScreen(val title: String, val subtitle: String) {
    HOME("1. Home Screen", "Calm clock, quote, mission card & essential dock"),
    NOTES_P1("2. Infinite Notes", "Signature 01/08 paper notebook auto-saving canvas"),
    NOTES_P2("3. Notes Second Page", "Seamless 02/08 swipe-transition notebook page"),
    APP_DRAWER("4. App Drawer", "Categorized monochrome grid & instant search"),
    GESTURE_OVERVIEW("5. Gesture Overview", "Interactive gesture map: Up, Right, Left, Long-press"),
    AMBIENT_LOCK("6. Ambient Lock", "Minimal ambient lock display with time and today's mission")
}

enum class IdentityTitle(val label: String, val subtitle: String) {
    THE_ARCHITECT("The Architect", "Forged by daily execution."),
    THE_BUILDER("The Builder", "Built through consistency."),
    THE_STRATEGIST("The Strategist", "Navigating long horizons with steady will."),
    THE_CREATOR("The Creator", "Synthesizing thoughts into tangible reality."),
    THE_COMMANDER("The Commander", "Total sovereignty over time and focus.")
}

data class LauncherUiState(
    val activeMission: TaskEntity? = null,
    val activeTasks: List<TaskEntity> = emptyList(),
    val completedTasks: List<TaskEntity> = emptyList(),
    val allTasksList: List<TaskEntity> = emptyList(),
    val selectedCalendarDate: Long = DateUtils.getStartOfDay(),
    val selectedClockStyle: ClockStyle = ClockStyle.NEO_GLASS,
    val todayFocusSeconds: Int = 0,
    val todayCompletedCount: Int = 0,
    val allApps: List<AppItem> = emptyList(),
    val pinnedApps: List<AppItem> = emptyList(),
    val appSearchQuery: String = "",
    val selectedAppCategory: AppCategory = AppCategory.ALL,
    val todayTasks: List<TaskEntity> = emptyList(),
    val selectedDateTasks: List<TaskEntity> = emptyList(),
    val filteredApps: List<AppItem> = emptyList(),
    val notes: List<NoteEntity> = emptyList(),
    val isAppDrawerOpen: Boolean = false,
    val isInfiniteNotesOpen: Boolean = false,
    val infiniteNotesInitialPage: Int = 0,
    val isFutureDashboardOpen: Boolean = false,
    val isGestureOverviewOpen: Boolean = false,
    val isTaskEngineOpen: Boolean = false,
    val isMissionsOpen: Boolean = false,
    val isProgressOpen: Boolean = false,
    val isAiCoachOpen: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val isAppearanceStudioOpen: Boolean = false,
    val appearanceState: AppearanceState = AppearanceState(),
    val isAmbientLockOpen: Boolean = false,
    val isOnboardingOpen: Boolean = false,
    val userIntention: String = "Build a Project",
    val isFocusModeActive: Boolean = false,
    val focusTimerSecondsRemaining: Int = 25 * 60,
    val focusTimerInitialSeconds: Int = 25 * 60,
    val isFocusTimerRunning: Boolean = false,
    val isBreathingPulseActive: Boolean = true,
    val selectedFocusModePreset: FocusPreset = FocusPreset.POMODORO_25,
    val currentMockupPreview: MockupScreen? = null,
    // Customization & Personalization
    val profileName: String = "Alex",
    val selectedColorPalette: ColorPalette = ColorPalette.OBSIDIAN_CRIMSON,
    val appIconSize: AppIconSize = AppIconSize.DEFAULT,
    val hideAppLabels: Boolean = false,
    val hideDockLabels: Boolean = true,
    val drawerGridColumns: Int = 4,
    val aiNudgesEnabled: Boolean = true,
    val ambientSoundEnabled: Boolean = true,
    val exitFrictionEnabled: Boolean = true,
    // Wallpaper & Atmosphere
    val selectedWallpaper: BuiltInWallpaper = BuiltInWallpaper.OBSIDIAN_VOID,
    val customWallpaperPath: String? = null,
    val wallpaperDim: Float = 0.35f,
    // Evolution & Identity System
    val customAvatarPath: String? = null,
    val identityTitle: IdentityTitle = IdentityTitle.THE_ARCHITECT,
    val identityVocation: String = "Developer",
    val currentFormTitle: String = "Focused Starter",
    val nextEvolutionTitle: String = "Level 2 • Disciplined Builder",
    val momentumDays: Int = 1,
    val focusAttribute: Float = 0.20f,
    val disciplineAttribute: Float = 0.20f,
    val executionAttribute: Float = 0.20f,
    val consistencyAttribute: Float = 0.20f,
    val knowledgeAttribute: Float = 0.20f,
    val leadershipAttribute: Float = 0.20f,
    val energyAttribute: Float = 0.30f,
    val isLevelUpModalOpen: Boolean = false,
    val userTotalXp: Int = 0,
    val claimedTodayXp: Boolean = false,
    val unlockedBadges: Set<String> = setOf("first_step")
) {
    val todayFocusMinutes: Int
        get() = todayFocusSeconds / 60

    companion object {
        fun computeTodayTasks(active: List<TaskEntity>, completed: List<TaskEntity>): List<TaskEntity> {
            val now = System.currentTimeMillis()
            return (active + completed).filter { task ->
                DateUtils.isSameDay(task.scheduledDate, now) ||
                        (!task.isCompleted && task.scheduledDate < now)
            }.sortedWith(
                compareBy<TaskEntity> { it.isCompleted }
                    .thenByDescending { it.isMission }
                    .thenByDescending { it.priority }
                    .thenByDescending { it.createdAt }
            )
        }

        fun computeSelectedDateTasks(
            active: List<TaskEntity>,
            completed: List<TaskEntity>,
            selectedDate: Long
        ): List<TaskEntity> {
            return (active + completed).filter { task ->
                DateUtils.isSameDay(task.scheduledDate, selectedDate)
            }.sortedWith(
                compareBy<TaskEntity> { it.isCompleted }
                    .thenByDescending { it.isMission }
                    .thenByDescending { it.priority }
                    .thenByDescending { it.createdAt }
            )
        }

        fun computeFilteredApps(
            apps: List<AppItem>,
            category: AppCategory,
            query: String
        ): List<AppItem> {
            var list = apps
            if (category != AppCategory.ALL) {
                list = list.filter { it.category == category }
            }
            if (query.isNotBlank()) {
                val q = query.trim().lowercase()
                list = list.filter {
                    it.name.lowercase().contains(q) || it.packageName.lowercase().contains(q)
                }
            }
            return list
        }
    }
}

enum class FocusPreset(val label: String, val minutes: Int) {
    MICRO_10("10m Sprint", 10),
    POMODORO_25("25m Focus", 25),
    DEEP_50("50m Deep Work", 50),
    FLOW_90("90m Flow State", 90)
}
