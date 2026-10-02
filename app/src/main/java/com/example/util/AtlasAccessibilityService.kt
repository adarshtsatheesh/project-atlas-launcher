package com.example.util

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.view.accessibility.AccessibilityEvent

/**
 * Modern Android Accessibility Service used to perform system-level actions
 * like locking the real device screen (GLOBAL_ACTION_LOCK_SCREEN on Android 9+).
 * This preserves biometric fingerprint and face unlock without screen timeout delays.
 */
class AtlasAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No-op: Only used for performing global system actions
    }

    override fun onInterrupt() {
        // No-op
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }

    companion object {
        var instance: AtlasAccessibilityService? = null
            private set

        fun isServiceRunning(): Boolean = instance != null

        fun lockDevice(): Boolean {
            val service = instance ?: return false
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            } else {
                false
            }
        }
    }
}
