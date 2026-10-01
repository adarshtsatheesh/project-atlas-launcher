package com.example.data.repository

import com.example.data.local.dao.FocusDao
import com.example.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class FocusRepository(private val focusDao: FocusDao) {

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

    val todaySessions: Flow<List<FocusSessionEntity>> =
        focusDao.getSessionsSince(startOfTodayMillis)

    val todayFocusSeconds: Flow<Int> =
        focusDao.getTotalFocusSecondsSince(startOfTodayMillis)

    suspend fun recordSession(
        taskId: Long?,
        taskTitle: String,
        durationSeconds: Int,
        mode: String = "POMODORO",
        completedSuccessfully: Boolean = true
    ): Long {
        val session = FocusSessionEntity(
            taskId = taskId,
            taskTitle = taskTitle,
            durationSeconds = durationSeconds,
            mode = mode,
            completedSuccessfully = completedSuccessfully,
            timestamp = System.currentTimeMillis()
        )
        return focusDao.insertSession(session)
    }
}
