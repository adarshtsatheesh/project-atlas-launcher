package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pinned_apps")
data class PinnedAppEntity(
    @PrimaryKey
    val packageName: String,
    val label: String,
    val orderIndex: Int = 0,
    val launchCount: Int = 0,
    val lastLaunched: Long = 0L
)
