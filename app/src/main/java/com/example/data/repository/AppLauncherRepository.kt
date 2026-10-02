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
import com.example.data.local.dao.PinnedAppDao
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
import kotlinx.coroutines.withContext

class AppLauncherRepository(
    private val context: Context,
    private val pinnedAppDao: PinnedAppDao
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

        // Clean up any pinned apps in database that are no longer installed on the device!
        val validKeys = finalApps.flatMap { listOf(it.uniqueKey, it.packageName) }
        if (validKeys.isNotEmpty()) {
            try {
                pinnedAppDao.removeUninstalledPinnedApps(validKeys)
            } catch (e: Exception) {
                // Ignore any DB cleanup issues
            }
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
        try {
            pinnedAppDao.unpinApp(packageName)
        } catch (e: Exception) {
            // Ignore
        }
        refreshApps()
    }

    val pinnedApps: Flow<List<PinnedAppEntity>> = pinnedAppDao.getAllPinnedApps()

    val combinedApps: Flow<List<AppItem>> = combine(
        _installedApps,
        pinnedApps
    ) { installedList, pinnedList ->
        val pinnedMap = pinnedList.associateBy { it.packageName }
        installedList.map { app ->
            val pinnedEntity = pinnedMap[app.uniqueKey] ?: pinnedMap[app.packageName]
            app.copy(
                isPinned = pinnedEntity != null,
                launchCount = pinnedEntity?.launchCount ?: 0
            )
        }
    }.flowOn(Dispatchers.Default)

    suspend fun togglePin(app: AppItem) {
        val pinKey = if (app.isCloned) app.uniqueKey else app.packageName
        if (app.isPinned) {
            pinnedAppDao.unpinApp(pinKey)
            pinnedAppDao.unpinApp(app.packageName)
        } else {
            pinnedAppDao.insertOrUpdate(
                PinnedAppEntity(
                    packageName = pinKey,
                    label = app.name,
                    orderIndex = 0
                )
            )
        }
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
