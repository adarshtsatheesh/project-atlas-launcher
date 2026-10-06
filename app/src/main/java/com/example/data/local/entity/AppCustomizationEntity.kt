package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Launcher-level personalisation for an app: a custom label ("Edit name") and an
 * icon override (built-in preset or a gallery image picked by the user).
 *
 * Keyed by [AppItem.uniqueKey] so cloned / dual apps keep their own identity.
 * A row only exists while at least one override is active — "Reset to original"
 * deletes it.
 */
@Entity(tableName = "app_customizations")
data class AppCustomizationEntity(
    @PrimaryKey
    val uniqueKey: String,
    val customLabel: String? = null,
    val iconPresetId: Int? = null,
    val customIconUri: String? = null,
    val updatedAt: Long = 0L
)
