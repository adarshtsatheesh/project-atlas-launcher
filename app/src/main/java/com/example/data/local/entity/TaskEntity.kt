package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val isMission: Boolean = false,
    val priority: Int = 1, // 1 = Standard, 2 = Priority, 3 = Urgent
    val estimatedMinutes: Int = 25,
    val focusMinutesSpent: Int = 0,
    val microStepsRaw: String = "", // Delimited by "\n" for simplicity and robustness
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledDate: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    fun getMicroSteps(): List<Pair<String, Boolean>> {
        if (microStepsRaw.isBlank()) return emptyList()
        return microStepsRaw.lines().filter { it.isNotBlank() }.map { line ->
            if (line.startsWith("[x] ")) {
                line.removePrefix("[x] ") to true
            } else if (line.startsWith("[ ] ")) {
                line.removePrefix("[ ] ") to false
            } else {
                line to false
            }
        }
    }

    companion object {
        fun formatMicroSteps(steps: List<Pair<String, Boolean>>): String {
            return steps.joinToString("\n") { (text, done) ->
                if (done) "[x] $text" else "[ ] $text"
            }
        }
    }
}
