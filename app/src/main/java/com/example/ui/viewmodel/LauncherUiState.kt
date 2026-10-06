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
import com.example.util.WallpaperTarget
import com.example.util.WeatherSnapshot

enum class ClockStyle(val label: String) {
    NEO_GLASS("Neo Glass"),
    STACKED_DIGITAL("Stacked Lockscreen"),
    CHRONOGRAPH("Glass Chrono")
}

/** Tone of the Gemini coach, passed into the model's system instruction. */
enum class CoachTone(val label: String, val instruction: String) {
    GENTLE("Gentle", "Warm, encouraging and forgiving. Celebrate small wins."),
    BALANCED("Balanced", "Calm, pragmatic and encouraging without being soft."),
    DIRECT("Direct", "Blunt, high-accountability and no-nonsense. Skip the cushioning.")
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

enum class EditCategory(val label: String) {
    WALLPAPER("Wallpaper"),
    APPEARANCE("Theme"),
    ICONS("Icons"),
    CLOCK("Clock"),
    LAYOUT("Layout"),
    TYPOGRAPHY("Font"),
    MORE("More");

    companion object {
        val THEME get() = APPEARANCE
        val FONT get() = TYPOGRAPHY
    }
}

/**
 * One day in the momentum strip: what really happened, not what was planned.
 * [active] is derived from tasks that were actually completed that day.
 */
data class DayActivity(
    val label: String,
    val dayOfMonth: Int,
    val active: Boolean,
    val isToday: Boolean
)

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
    val isEditMode: Boolean = false,
    val activeEditCategory: EditCategory? = null,
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
    val coachTone: CoachTone = CoachTone.BALANCED,
    val defaultFocusPreset: FocusPreset = FocusPreset.POMODORO_25,
    // Wallpaper & Atmosphere
    val selectedWallpaper: BuiltInWallpaper = BuiltInWallpaper.OBSIDIAN_VOID,
    val customWallpaperPath: String? = null,
    val wallpaperDim: Float = 0.35f,
    // Which system surfaces the wallpaper is applied to (home / lock / both)
    val wallpaperTarget: WallpaperTarget = WallpaperTarget.BOTH,
    // Dedicated lock screen wallpaper; null = "same as home"
    val lockWallpaper: BuiltInWallpaper? = null,
    val lockCustomWallpaperPath: String? = null,
    // Live local weather for the clock header (null until first fetch)
    val weather: WeatherSnapshot? = null,
    // Evolution & Identity System
    val customAvatarPath: String? = null,
    val identityTitle: IdentityTitle = IdentityTitle.THE_ARCHITECT,
    val identityVocation: String = "Developer",
    val currentFormTitle: String = "Focused Starter",
    val nextEvolutionTitle: String = "Level 2 • Disciplined Builder",
    val momentumDays: Int = 1,
    /** Every day with a real action, as yyyyMMdd keys — the streak's source of truth. */
    val activeDayKeys: Set<String> = emptySet(),
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

    /**
     * Real user context injected into every Gemini request so coaching is specific
     * to the current mission, streak and daily numbers instead of generic.
     */
    val coachContext: String
        get() = buildString {
            appendLine("Coaching tone: ${coachTone.instruction}")
            appendLine(
                "User: $profileName. Identity: ${identityTitle.label}. " +
                    "Momentum streak: $momentumDays days. Total XP: $userTotalXp."
            )
            appendLine(
                "Today: $todayFocusMinutes minutes of focus, $todayCompletedCount tasks completed."
            )
            val mission = activeMission
            if (mission != null) {
                append("Active mission: ${mission.title}.")
                if (mission.description.isNotBlank()) {
                    append(" Details: ${mission.description}")
                }
                val steps = mission.getMicroSteps()
                if (steps.isNotEmpty()) {
                    append(" Micro-steps: ")
                    append(steps.joinToString(" | ") { (text, done) ->
                        (if (done) "[done] " else "[open] ") + text
                    })
                }
            } else {
                append("No active mission yet.")
            }
        }

    companion object {
        /** Visible dock slots on the home screen and in the drawer dock strip. */
        const val MAX_DOCK_APPS = 5

        /**
         * Maps persisted pin keys (in stored order) onto the live installed-app
         * list. The result keeps the exact user arrangement and silently skips
         * pins whose app is temporarily missing, instead of resetting the dock.
         */
        fun orderPinnedApps(apps: List<AppItem>, pinKeys: List<String>): List<AppItem> {
            if (pinKeys.isEmpty() || apps.isEmpty()) return emptyList()
            val byUniqueKey = apps.associateBy { it.uniqueKey }
            val ordered = ArrayList<AppItem>(pinKeys.size)
            val seen = HashSet<String>(pinKeys.size)
            for (key in pinKeys) {
                val match = byUniqueKey[key]
                    ?: apps.firstOrNull { !it.isCloned && it.packageName == key }
                    ?: apps.firstOrNull { it.packageName == key }
                    ?: continue
                if (seen.add(match.uniqueKey)) ordered.add(match)
            }
            return ordered
        }

        /**
         * The last [days] days of *real* execution (oldest first), derived from the
         * completion timestamps of finished tasks. Used by the System panel's
         * momentum strip, so the streak shown is earned rather than decorative.
         */
        fun computeWeekActivity(
            tasks: List<TaskEntity>,
            activeDayKeys: Set<String> = emptySet(),
            days: Int = 7
        ): List<DayActivity> {
            val finished = tasks.filter { it.isCompleted && (it.completedAt ?: 0L) > 0L }
            val labels = arrayOf("S", "M", "T", "W", "T", "F", "S")
            val keyFormat = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US)
            val result = ArrayList<DayActivity>(days)
            for (offset in days - 1 downTo 0) {
                val day = java.util.Calendar.getInstance()
                day.add(java.util.Calendar.DAY_OF_YEAR, -offset)
                val dayMillis = day.timeInMillis
                // A day counts when anything real happened: a completed task, a focus
                // block or a claimed daily reward — the same rule the streak uses.
                val active = activeDayKeys.contains(keyFormat.format(day.time)) ||
                        finished.any { DateUtils.isSameDay(it.completedAt ?: 0L, dayMillis) }
                result.add(
                    DayActivity(
                        label = labels[day.get(java.util.Calendar.DAY_OF_WEEK) - 1],
                        dayOfMonth = day.get(java.util.Calendar.DAY_OF_MONTH),
                        active = active,
                        isToday = offset == 0
                    )
                )
            }
            return result
        }

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
                    it.displayName.lowercase().contains(q) ||
                        it.name.lowercase().contains(q) ||
                        it.packageName.lowercase().contains(q)
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
