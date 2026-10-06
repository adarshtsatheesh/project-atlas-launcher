package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.example.domain.model.AppItem

/**
 * Thin bridge to the *real* operating-system surfaces — App info, Uninstall and
 * the system share sheet — instead of re-implementing them. Every action is a
 * single `startActivity` on an OS intent, so it stays instant and always matches
 * the device's own UI.
 */
object AppSystemActions {

    /** Opens the system "App info" settings page for [app]. */
    fun openAppInfo(context: Context, app: AppItem) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", app.packageName, null)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        start(context, intent)
    }

    /**
     * Launches the package installer's real uninstall flow. For system apps the
     * OS itself falls back to its App info screen (disable / uninstall updates),
     * which is exactly the platform behaviour.
     */
    fun uninstallApp(context: Context, app: AppItem) {
        val intent = Intent(
            Intent.ACTION_DELETE,
            Uri.fromParts("package", app.packageName, null)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        start(context, intent) {
            // Rare (device-policy / system app) fallback: show the real App info.
            openAppInfo(context, app)
        }
    }

    /** Opens the system share sheet with this app's name and store link. */
    fun shareApp(context: Context, app: AppItem) {
        val storeLink = "https://play.google.com/store/apps/details?id=${app.packageName}"
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, app.displayName)
            putExtra(Intent.EXTRA_TEXT, "${app.displayName}\n$storeLink")
        }
        val chooser = Intent.createChooser(send, "Share ${app.displayName}")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        start(context, chooser)
    }

    private fun start(context: Context, intent: Intent, onFailure: (() -> Unit)? = null) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            onFailure?.invoke()
                ?: Toast.makeText(context, "Not supported for this app", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            onFailure?.invoke()
                ?: Toast.makeText(context, "Not supported for this app", Toast.LENGTH_SHORT).show()
        }
    }
}
