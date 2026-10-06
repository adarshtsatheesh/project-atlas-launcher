package com.example.domain.model

import android.content.ComponentName
import android.graphics.drawable.Drawable
import android.os.UserHandle

enum class AppCategory(val label: String) {
    ALL("All"),
    WORK("Work"),
    SOCIAL("Social"),
    MEDIA("Media"),
    TOOLS("Tools")
}

data class AppItem(
    val packageName: String,
    val name: String,
    val icon: Drawable? = null,
    val isPinned: Boolean = false,
    val launchCount: Int = 0,
    val category: AppCategory = AppCategory.TOOLS,
    val userHandle: UserHandle? = null,
    val componentName: ComponentName? = null,
    val isCloned: Boolean = false,
    val isWorkProfile: Boolean = false,
    // Launcher-level user edits (see AppCustomizationEntity). Null = system default.
    val customLabel: String? = null,
    val iconPresetId: Int? = null,
    val customIconUri: String? = null
) {
    val uniqueKey: String
        get() = if (userHandle != null) "${packageName}#${userHandle.hashCode()}" else packageName

    /** Label the launcher renders: the user's alias when set, the real app name otherwise. */
    val displayName: String
        get() = customLabel?.takeIf { it.isNotBlank() } ?: name

    /** True when the user replaced the system icon with a preset or a gallery image. */
    val hasCustomIcon: Boolean
        get() = iconPresetId != null || !customIconUri.isNullOrBlank()

    companion object {
        fun resolveCategory(name: String, packageName: String): AppCategory {
            val lower = (name + " " + packageName).lowercase()
            return when {
                lower.contains("work") || lower.contains("gmail") || lower.contains("mail") ||
                        lower.contains("slack") || lower.contains("calendar") || lower.contains("notion") ||
                        lower.contains("linear") || lower.contains("docs") || lower.contains("sheets") ||
                        lower.contains("drive") || lower.contains("keep") || lower.contains("teams") ||
                        lower.contains("outlook") || lower.contains("tasks") || lower.contains("github") ||
                        lower.contains("office") -> AppCategory.WORK

                lower.contains("social") || lower.contains("messages") || lower.contains("chat") ||
                        lower.contains("whatsapp") || lower.contains("telegram") || lower.contains("phone") ||
                        lower.contains("contacts") || lower.contains("twitter") || lower.contains("discord") ||
                        lower.contains("instagram") || lower.contains("linkedin") || lower.contains("reddit") ||
                        lower.contains("dialer") -> AppCategory.SOCIAL

                lower.contains("media") || lower.contains("camera") || lower.contains("photos") ||
                        lower.contains("gallery") || lower.contains("spotify") || lower.contains("youtube") ||
                        lower.contains("music") || lower.contains("netflix") || lower.contains("video") ||
                        lower.contains("podcast") || lower.contains("recorder") -> AppCategory.MEDIA

                else -> AppCategory.TOOLS
            }
        }
    }
}
