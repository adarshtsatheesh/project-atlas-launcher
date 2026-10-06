package com.example.data.repository

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.os.UserHandle
import android.os.UserManager
import com.example.data.local.dao.AppCustomizationDao
import com.example.data.local.dao.PinnedAppDao
import com.example.data.local.entity.AppCustomizationEntity
import com.example.data.local.entity.PinnedAppEntity
import com.example.domain.model.AppItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AppLauncherRepository(
    private val context: Context,
    private val pinnedAppDao: PinnedAppDao,
    private val appCustomizationDao: AppCustomizationDao,
    /**
     * True only on a brand-new install (no database file yet). Seeds the classic
     * four-app dock as *real, removable pins* exactly once — existing users keep
     * whatever dock they had, even an empty one.
     */
    private val seedDefaultDock: Boolean = false
) {
    private val packageManager: PackageManager = context.packageManager
    private val selfPackageName: String = context.packageName
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())
    val installedApps: StateFlow<List<AppItem>> = _installedApps.asStateFlow()

    private val launcherAppsCallback = object : LauncherApps.Callback() {
        override fun onPackageAdded(packageName: String, user: UserHandle) {
            repositoryScope.launch { refreshApps() }
        }

        override fun onPackageRemoved(packageName: String, user: UserHandle) {
            repositoryScope.launch { onPackageRemoved(packageName, user) }
        }

        override fun onPackageChanged(packageName: String, user: UserHandle) {
            repositoryScope.launch { refreshApps() }
        }

        override fun onPackagesAvailable(packageNames: Array<out String>, user: UserHandle, replacing: Boolean) {
            repositoryScope.launch { refreshApps() }
        }

        override fun onPackagesUnavailable(packageNames: Array<out String>, user: UserHandle, replacing: Boolean) {
            repositoryScope.launch { refreshApps() }
        }
    }

    init {
        try {
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
            launcherApps?.registerCallback(launcherAppsCallback)
        } catch (e: Exception) {
            // Ignore if callback registration is unsupported
        }

        repositoryScope.launch {
            refreshApps()
            if (seedDefaultDock) seedDefaultDockIfNeeded()
        }
    }

    suspend fun refreshApps(): List<AppItem> = withContext(Dispatchers.IO) {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
        val userManager = context.getSystemService(Context.USER_SERVICE) as? UserManager
        val myUser = Process.myUserHandle()

        // Discover all user profiles: primary, cloned apps (Dual Messenger / Dual Apps), and work profiles
        val profiles: List<UserHandle> = try {
            userManager?.userProfiles ?: listOf(myUser)
        } catch (e: Exception) {
            listOf(myUser)
        }

        val density = context.resources.displayMetrics.densityDpi
        val apps = mutableListOf<AppItem>()

        if (launcherApps != null && profiles.isNotEmpty()) {
            for (profile in profiles) {
                val isCloned = (profile != myUser)
                val isWork = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        userManager?.isManagedProfile == true && isCloned
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    false
                }

                val activityList = try {
                    launcherApps.getActivityList(null, profile)
                } catch (e: Exception) {
                    emptyList()
                }

                for (activity in activityList) {
                    val pkg = activity.applicationInfo.packageName ?: continue
                    if (pkg == selfPackageName) continue

                    val rawLabel = try {
                        activity.label?.toString() ?: pkg
                    } catch (e: Exception) {
                        pkg
                    }

                    val displayName = if (isCloned) {
                        if (isWork) "$rawLabel (Work)" else "$rawLabel (Dual)"
                    } else {
                        rawLabel
                    }

                    val icon = try {
                        activity.getBadgedIcon(density)
                    } catch (e: Exception) {
                        try {
                            activity.getIcon(density)
                        } catch (e2: Exception) {
                            null
                        }
                    }

                    apps.add(
                        AppItem(
                            packageName = pkg,
                            name = displayName,
                            icon = icon,
                            isPinned = false,
                            category = AppItem.resolveCategory(rawLabel, pkg),
                            userHandle = profile,
                            componentName = activity.componentName,
                            isCloned = isCloned,
                            isWorkProfile = isWork
                        )
                    )
                }
            }
        }

        // Fallback for standard apps if launcherApps yielded nothing
        if (apps.isEmpty()) {
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = try {
                packageManager.queryIntentActivities(launcherIntent, 0)
            } catch (e: Exception) {
                emptyList()
            }

            apps.addAll(
                resolveInfos.mapNotNull { resolveInfo ->
                    val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                    val pkg = activityInfo.packageName ?: return@mapNotNull null
                    if (pkg == selfPackageName) return@mapNotNull null
                    val label = try {
                        resolveInfo.loadLabel(packageManager).toString()
                    } catch (e: Exception) {
                        pkg
                    }
                    val icon = try {
                        resolveInfo.loadIcon(packageManager)
                    } catch (e: Exception) {
                        null
                    }
                    AppItem(
                        packageName = pkg,
                        name = label,
                        icon = icon,
                        isPinned = false,
                        category = AppItem.resolveCategory(label, pkg),
                        userHandle = myUser,
                        componentName = ComponentName(pkg, activityInfo.name),
                        isCloned = false
                    )
                }
            )
        }

        val sortedApps = apps.sortedWith(
            compareBy({ it.name.lowercase() }, { it.isCloned })
        )

        val finalApps = if (sortedApps.isNotEmpty()) {
            sortedApps
        } else {
            val defaultList = listOf(
                Pair("com.google.android.dialer", "Phone"),
                Pair("com.google.android.apps.messaging", "Messages"),
                Pair("com.android.chrome", "Browser"),
                Pair("com.google.android.GoogleCamera", "Camera"),
                Pair("com.google.android.calendar", "Calendar"),
                Pair("com.google.android.deskclock", "Clock"),
                Pair("com.google.android.keep", "Notes"),
                Pair("com.android.settings", "Settings")
            )
            defaultList.mapNotNull { (pkg, label) ->
                val launchIntent = packageManager.getLaunchIntentForPackage(pkg)
                if (launchIntent != null) {
                    val realIcon = runCatching { packageManager.getApplicationIcon(pkg) }.getOrNull()
                    AppItem(
                        packageName = pkg,
                        name = label,
                        icon = realIcon,
                        isPinned = label in listOf("Phone", "Messages", "Browser", "Camera"),
                        category = AppItem.resolveCategory(label, pkg)
                    )
                } else null
            }
        }

        // Prune pins whose package is genuinely gone from the device.
        // (The old code mass-deleted every pin missing from a single scan — a
        // partial/early boot scan silently wiped the whole dock.)
        if (finalApps.isNotEmpty()) {
            pruneUninstalledPins()
        }

        // Pre-warm AppIconCache on background IO thread so Compose UI renders 120 FPS instantly
        for (app in finalApps) {
            com.example.util.AppIconCache.getOrConvert(app.uniqueKey, app.icon)
        }

        _installedApps.value = finalApps
        finalApps
    }

    suspend fun onPackageRemoved(packageName: String, userHandle: UserHandle? = null) = withContext(Dispatchers.IO) {
        // Immediately purge from in-memory state for instant UI update
        _installedApps.update { list ->
            if (userHandle != null) {
                list.filterNot { it.packageName == packageName && it.userHandle == userHandle }
            } else {
                list.filterNot { it.packageName == packageName }
            }
        }
        // Do NOT unpin eagerly: a single profile's removal (or an app update)
        // must never drop the dock pin. refreshApps() prunes only packages that
        // are really gone from every profile.
        refreshApps()
    }

    val pinnedApps: Flow<List<PinnedAppEntity>> = pinnedAppDao.getAllPinnedApps()

    private val appCustomizations: Flow<List<AppCustomizationEntity>> = appCustomizationDao.getAll()

    val combinedApps: Flow<List<AppItem>> = combine(
        _installedApps,
        pinnedApps,
        appCustomizations
    ) { installedList, pinnedList, customList ->
        val pinnedMap = pinnedList.associateBy { it.packageName }
        val customMap = customList.associateBy { it.uniqueKey }
        installedList.map { app ->
            val pinnedEntity = pinnedMap[app.uniqueKey] ?: pinnedMap[app.packageName]
            val custom = customMap[app.uniqueKey] ?: customMap[app.packageName]
            app.copy(
                isPinned = pinnedEntity != null,
                launchCount = pinnedEntity?.launchCount ?: 0,
                customLabel = custom?.customLabel,
                iconPresetId = custom?.iconPresetId,
                customIconUri = custom?.customIconUri
            )
        }
    }.flowOn(Dispatchers.Default)

    // ---------------------------------------------------------------------
    // Dock (pinned apps) — order is stored explicitly and never recomputed
    // from the alphabetical app list, so the user's arrangement sticks.
    // ---------------------------------------------------------------------

    private val pinMutex = Mutex()

    private fun pinKeyFor(app: AppItem): String =
        if (app.isCloned) app.uniqueKey else app.packageName

    /**
     * Pin [app] at [index] (0 = far left), or move it there when it is already
     * pinned. A null [index] appends to the end of the dock.
     */
    suspend fun pinAppAt(app: AppItem, index: Int? = null) = pinMutex.withLock {
        val key = pinKeyFor(app)
        val current = pinnedAppDao.getPinnedAppsOnce().toMutableList()
        val existingIndex = current.indexOfFirst { it.packageName == key }
        val insertAt = (index ?: (if (existingIndex >= 0) existingIndex else current.size))
            .coerceIn(0, current.size)

        if (existingIndex >= 0) {
            if (existingIndex == insertAt) return@withLock
            val item = current.removeAt(existingIndex)
            current.add((if (insertAt > existingIndex) insertAt - 1 else insertAt).coerceIn(0, current.size), item)
        } else {
            current.add(
                insertAt,
                PinnedAppEntity(packageName = key, label = app.name, orderIndex = insertAt)
            )
        }
        persistPinOrder(current)
    }

    /** Insertion-index reorder: [to] is the index in the list *before* removal. */
    suspend fun movePin(from: Int, to: Int) = pinMutex.withLock {
        val current = pinnedAppDao.getPinnedAppsOnce().toMutableList()
        if (from !in current.indices) return@withLock
        val item = current.removeAt(from)
        val target = (if (to > from) to - 1 else to).coerceIn(0, current.size)
        if (target == from) {
            current.add(from, item)
            return@withLock
        }
        current.add(target, item)
        persistPinOrder(current)
    }

    suspend fun removePin(app: AppItem) = pinMutex.withLock {
        pinnedAppDao.unpinApp(pinKeyFor(app))
        // Legacy compatibility: pins written by older builds used the plain
        // package name even for dual apps.
        if (app.isCloned) pinnedAppDao.unpinApp(app.packageName)
        persistPinOrder(pinnedAppDao.getPinnedAppsOnce())
    }

    private suspend fun persistPinOrder(list: List<PinnedAppEntity>) {
        try {
            // Rewrite membership + order together; an @Update alone never inserted
            // a newly pinned app, so pins silently vanished on the way to disk.
            pinnedAppDao.replacePins(list.mapIndexed { i, entity -> entity.copy(orderIndex = i) })
        } catch (e: Exception) {
            // Ignore persistence hiccups — the next write retries the order.
        }
    }

    // ---------------------------------------------------------------------
    // App customization (rename + icon override)
    // ---------------------------------------------------------------------

    suspend fun saveAppCustomization(
        app: AppItem,
        customLabel: String?,
        iconPresetId: Int?,
        customIconUri: String?
    ) = withContext(Dispatchers.IO) {
        try {
            if (customLabel == null && iconPresetId == null && customIconUri == null) {
                appCustomizationDao.delete(app.uniqueKey)
            } else {
                appCustomizationDao.upsert(
                    AppCustomizationEntity(
                        uniqueKey = app.uniqueKey,
                        customLabel = customLabel,
                        iconPresetId = iconPresetId,
                        customIconUri = customIconUri,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun resetAppCustomization(app: AppItem) = withContext(Dispatchers.IO) {
        try {
            appCustomizationDao.delete(app.uniqueKey)
        } catch (e: Exception) {
            // Ignore
        }
    }

    // ---------------------------------------------------------------------
    // Maintenance
    // ---------------------------------------------------------------------

    /**
     * Removes a pin only when its base package is no longer installed for any
     * profile. Safe against partial scans, work profiles and dual apps.
     */
    private suspend fun pruneUninstalledPins() {
        val pins = try {
            pinnedAppDao.getPinnedAppsOnce()
        } catch (e: Exception) {
            return
        }
        for (pin in pins) {
            val pkg = pin.packageName.substringBefore('#')
            if (!isPackageInstalledForAnyProfile(pkg)) {
                try {
                    pinnedAppDao.unpinApp(pin.packageName)
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
    }

    private fun isPackageInstalledForAnyProfile(packageName: String): Boolean {
        try {
            packageManager.getApplicationInfo(packageName, 0)
            return true
        } catch (e: Exception) {
            // Not visible to the primary user (work profile / dual app) — fall
            // back to the live launcher scan before declaring it gone.
        }
        return _installedApps.value.any { it.packageName == packageName }
    }

    /** First-run only: seed the classic dock as real pins the user can edit. */
    private suspend fun seedDefaultDockIfNeeded() {
        try {
            if (pinnedAppDao.getPinnedAppsOnce().isNotEmpty()) return
            val defaults = listOf(
                "Phone" to listOf("com.google.android.dialer", "com.android.dialer", "com.android.contacts"),
                "Messages" to listOf("com.google.android.apps.messaging", "com.android.mms", "com.android.messaging"),
                "Browser" to listOf("com.android.chrome", "com.google.android.browser", "com.android.browser"),
                "Camera" to listOf("com.google.android.GoogleCamera", "com.android.camera", "com.oplus.camera")
            )
            var index = 0
            for ((label, packages) in defaults) {
                val app = resolveDefaultDockApp(label, packages) ?: continue
                pinnedAppDao.insertOrUpdate(
                    PinnedAppEntity(packageName = app.packageName, label = app.name, orderIndex = index)
                )
                index++
            }
        } catch (e: Exception) {
            // Ignore — the user can always drag apps into the dock.
        }
    }

    private fun resolveDefaultDockApp(label: String, candidatePackages: List<String>): AppItem? {
        val installed = _installedApps.value
        installed.firstOrNull { !it.isCloned && it.name.equals(label, ignoreCase = true) }
            ?.let { return it }
        for (pkg in candidatePackages) {
            installed.firstOrNull { !it.isCloned && it.packageName == pkg }?.let { return it }
        }
        return null
    }

    suspend fun launchApp(app: AppItem): Boolean = withContext(Dispatchers.Main) {
        try {
            val options = ActivityOptions.makeBasic()
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
            if (launcherApps != null && app.componentName != null && app.userHandle != null) {
                try {
                    launcherApps.startMainActivity(app.componentName, app.userHandle, null, options.toBundle())
                    val key = if (app.isCloned) app.uniqueKey else app.packageName
                    pinnedAppDao.recordLaunch(key)
                    return@withContext true
                } catch (e: Exception) {
                    // Fallback to packageManager if startMainActivity encounters an issue
                }
            }

            val intent = packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                )
                context.startActivity(intent, options.toBundle())
                pinnedAppDao.recordLaunch(app.packageName)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun launchApp(packageName: String): Boolean = withContext(Dispatchers.Main) {
        val app = _installedApps.value.find { it.packageName == packageName || it.uniqueKey == packageName }
        if (app != null) {
            launchApp(app)
        } else {
            try {
                val options = ActivityOptions.makeBasic()
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                if (intent != null) {
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    )
                    context.startActivity(intent, options.toBundle())
                    pinnedAppDao.recordLaunch(packageName)
                    true
                } else false
            } catch (e: Exception) {
                false
            }
        }
    }
}
