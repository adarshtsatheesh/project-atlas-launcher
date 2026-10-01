package com.example.data.repository

import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val activeTasks: Flow<List<TaskEntity>> = taskDao.getActiveTasks()
    val completedTasks: Flow<List<TaskEntity>> = taskDao.getCompletedTasks()
    val activeMission: Flow<TaskEntity?> = taskDao.getActiveMission()

    private val startOfTodayMillis: Long
        get() {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }

    val todayCompletedCount: Flow<Int> = taskDao.getCompletedCountSince(startOfTodayMillis)

    suspend fun createTask(
        title: String,
        description: String = "",
        priority: Int = 1,
        estimatedMinutes: Int = 25,
        microSteps: List<String> = emptyList(),
        scheduledDate: Long = System.currentTimeMillis(),
        asMission: Boolean = false
    ): Long {
        val microStepsRaw = TaskEntity.formatMicroSteps(microSteps.map { it to false })
        val task = TaskEntity(
            title = title.trim(),
            description = description.trim(),
            priority = priority,
            estimatedMinutes = estimatedMinutes,
            microStepsRaw = microStepsRaw,
            scheduledDate = scheduledDate,
            isMission = asMission
        )
        val id = taskDao.insertTask(task)
        if (asMission) {
            taskDao.setActiveMission(id)
        }
        return id
    }

    suspend fun rescheduleTask(task: TaskEntity, newScheduledDate: Long) {
        val updated = task.copy(scheduledDate = newScheduledDate)
        taskDao.updateTask(updated)
    }

    suspend fun setMission(taskId: Long) {
        taskDao.setActiveMission(taskId)
    }

    suspend fun toggleTaskCompletion(task: TaskEntity) {
        val willBeCompleted = !task.isCompleted
        val updated = task.copy(
            isCompleted = willBeCompleted,
            isMission = if (willBeCompleted) false else task.isMission,
            completedAt = if (willBeCompleted) System.currentTimeMillis() else null
        )
        taskDao.updateTask(updated)
    }

    suspend fun toggleMicroStep(task: TaskEntity, stepIndex: Int) {
        val currentSteps = task.getMicroSteps().toMutableList()
        if (stepIndex in currentSteps.indices) {
            val (text, done) = currentSteps[stepIndex]
            currentSteps[stepIndex] = text to !done
            val updated = task.copy(
                microStepsRaw = TaskEntity.formatMicroSteps(currentSteps)
            )
            taskDao.updateTask(updated)
        }
    }

    suspend fun addMicroStep(task: TaskEntity, stepText: String) {
        if (stepText.isBlank()) return
        val currentSteps = task.getMicroSteps().toMutableList()
        currentSteps.add(stepText.trim() to false)
        val updated = task.copy(
            microStepsRaw = TaskEntity.formatMicroSteps(currentSteps)
        )
        taskDao.updateTask(updated)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun addFocusMinutes(taskId: Long, minutes: Int) {
        val task = taskDao.getTaskById(taskId) ?: return
        taskDao.updateTask(task.copy(focusMinutesSpent = task.focusMinutesSpent + minutes))
    }

    suspend fun seedInitialDataIfEmpty() {
        val firstTask = TaskEntity(
            title = "Launch Project Atlas MVP",
            description = "Test the calm, execution-first launcher workflow and complete your first deep focus block.",
            isMission = true,
            priority = 3,
            estimatedMinutes = 25,
            microStepsRaw = "[ ] Inspect active mission card\n[ ] Start a 25-minute focus session\n[ ] Experience minimal distraction launcher"
        )
        val secondTask = TaskEntity(
            title = "Draft quarterly goals",
            description = "Define 3 core high-leverage outcomes for this quarter.",
            isMission = false,
            priority = 2,
            estimatedMinutes = 45,
            microStepsRaw = "[ ] Review past retrospectives\n[ ] Outline Q3 North Star metric\n[ ] List blocker risks"
        )
        val id1 = taskDao.insertTask(firstTask)
        taskDao.insertTask(secondTask)
        taskDao.setActiveMission(id1)
    }
}
