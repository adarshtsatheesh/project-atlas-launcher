package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long? = null,
    val taskTitle: String = "",
    val durationSeconds: Int,
    val mode: String = "POMODORO", // "POMODORO", "DEEP_WORK", "FLOW"
    val completedSuccessfully: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
