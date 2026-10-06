package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AtlasDatabase
import com.example.data.repository.AppLauncherRepository
import com.example.data.repository.FocusRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.TaskRepository
import com.example.ui.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels {
        // Seed the classic four-app dock ONCE: on a brand new install, or as a
        // one-time recovery for installs whose dock was already emptied by the
        // old reset bug. The flag makes sure it never happens again — after
        // this the dock only changes when the user changes it.
        val prefs = getSharedPreferences("atlas_launcher_prefs", Context.MODE_PRIVATE)
        val isFreshInstall = !getDatabasePath("atlas_launcher.db").exists()
        val shouldSeedDefaultDock = isFreshInstall || !prefs.getBoolean("dock_seeded_v1", false)
        if (shouldSeedDefaultDock) {
            prefs.edit().putBoolean("dock_seeded_v1", true).apply()
        }
        val database = AtlasDatabase.getInstance(applicationContext)
        val taskRepository = TaskRepository(database.taskDao())
        val focusRepository = FocusRepository(database.focusDao())
        val appLauncherRepository = AppLauncherRepository(
            context = applicationContext,
            pinnedAppDao = database.pinnedAppDao(),
            appCustomizationDao = database.appCustomizationDao(),
            seedDefaultDock = shouldSeedDefaultDock
        )
        val noteRepository = NoteRepository(database.noteDao())
        LauncherViewModel.Factory(taskRepository, focusRepository, appLauncherRepository, noteRepository, applicationContext)
    }

    private var isReceiverRegistered = false
    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            val packageName = intent.data?.schemeSpecificPart
            when (action) {
                Intent.ACTION_PACKAGE_REMOVED,
                Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                    // An app *update* also broadcasts ACTION_PACKAGE_REMOVED with
                    // EXTRA_REPLACING=true — treating it as a removal used to
                    // unpin dock apps every time an app updated.
                    val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                    if (!packageName.isNullOrBlank() && !replacing) {
                        viewModel.onPackageRemoved(packageName)
                    } else {
                        viewModel.refreshApps()
                    }
                }
                Intent.ACTION_PACKAGE_ADDED,
                Intent.ACTION_PACKAGE_REPLACED,
                Intent.ACTION_PACKAGE_CHANGED -> {
                    viewModel.refreshApps()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)

        try {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addAction(Intent.ACTION_PACKAGE_CHANGED)
                addDataScheme("package")
            }
            registerReceiver(packageReceiver, filter)
            isReceiverRegistered = true
        } catch (e: Exception) {
            // Ignore if registration fails
        }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            MyApplicationTheme(
                palette = uiState.selectedColorPalette,
                appearance = uiState.appearanceState
            ) {
                HomeScreen(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(packageReceiver)
                isReceiverRegistered = false
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}

