package com.example.util

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings

object LockScreenManager {

    /**
     * Checks whether the user has granted either the Accessibility Service
     * or Device Admin permission to allow locking the screen.
     */
    fun isLockPermissionGranted(context: Context): Boolean {
        if (AtlasAccessibilityService.isServiceRunning()) {
            return true
        }
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        val admin = ComponentName(context, AtlasDeviceAdminReceiver::class.java)
        return dpm?.isAdminActive(admin) == true
    }

    /**
     * Locks the real mobile screen directly.
     * Returns true if the screen lock action was dispatched successfully,
     * or false if permission has not been granted yet.
     */
    fun lockDevice(context: Context): Boolean {
        // 1. Primary method: Accessibility Service (Preserves biometric / fingerprint unlock)
        if (AtlasAccessibilityService.lockDevice()) {
            return true
        }

        // 2. Secondary method: Device Admin policy
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        val admin = ComponentName(context, AtlasDeviceAdminReceiver::class.java)
        if (dpm != null && dpm.isAdminActive(admin)) {
            try {
                dpm.lockNow()
                return true
            } catch (e: Exception) {
                // Fallback
            }
        }

        return false
    }

    /**
     * Directs the user to Android Accessibility settings to enable Atlas for screen locking.
     */
    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        }
    }
}
