package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CalendarDay(
    val dayOfMonth: Int,
    val timeMillis: Long,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val dayOfWeek: Int // Calendar.SUNDAY ... Calendar.SATURDAY
)

object DateUtils {

    fun getStartOfDay(timeMillis: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun isSameDay(t1: Long, t2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isToday(timeMillis: Long): Boolean {
        return isSameDay(timeMillis, System.currentTimeMillis())
    }

    fun formatDateHeader(timeMillis: Long): String {
        val today = getStartOfDay(System.currentTimeMillis())
        val target = getStartOfDay(timeMillis)
        val diffDays = ((target - today) / (24 * 60 * 60 * 1000L)).toInt()
        return when (diffDays) {
            0 -> "Today"
            1 -> "Tomorrow"
            -1 -> "Yesterday"
            in 2..6 -> SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date(timeMillis))
            else -> SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(Date(timeMillis))
        }
    }

    fun formatShortMonthDay(timeMillis: Long): String {
        return SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timeMillis))
    }

    fun formatMonthYear(year: Int, month: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
    }

    /**
     * Generates a list of CalendarDay objects representing the days to display in a month grid.
     * Starts with Monday as first day of week.
     */
    fun getMonthDays(year: Int, month: Int): List<CalendarDay> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        // In Calendar, SUNDAY=1, MONDAY=2, ..., SATURDAY=7
        // Convert to Monday=0, Tuesday=1 ... Sunday=6
        val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7

        val list = mutableListOf<CalendarDay>()

        // Previous month days to pad
        if (firstDayOfWeek > 0) {
            val prevCal = (cal.clone() as Calendar).apply {
                add(Calendar.MONTH, -1)
            }
            val prevMax = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in (firstDayOfWeek - 1) downTo 0) {
                val dayNum = prevMax - i
                prevCal.set(Calendar.DAY_OF_MONTH, dayNum)
                list.add(
                    CalendarDay(
                        dayOfMonth = dayNum,
                        timeMillis = prevCal.timeInMillis,
                        isCurrentMonth = false,
                        isToday = isToday(prevCal.timeInMillis),
                        dayOfWeek = prevCal.get(Calendar.DAY_OF_WEEK)
                    )
                )
            }
        }

        // Current month days
        for (day in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            list.add(
                CalendarDay(
                    dayOfMonth = day,
                    timeMillis = cal.timeInMillis,
                    isCurrentMonth = true,
                    isToday = isToday(cal.timeInMillis),
                    dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                )
            )
        }

        // Next month days to pad grid to multiple of 7
        val remaining = (7 - (list.size % 7)) % 7
        if (remaining > 0) {
            val nextCal = (cal.clone() as Calendar).apply {
                add(Calendar.MONTH, 1)
            }
            for (day in 1..remaining) {
                nextCal.set(Calendar.DAY_OF_MONTH, day)
                list.add(
                    CalendarDay(
                        dayOfMonth = day,
                        timeMillis = nextCal.timeInMillis,
                        isCurrentMonth = false,
                        isToday = isToday(nextCal.timeInMillis),
                        dayOfWeek = nextCal.get(Calendar.DAY_OF_WEEK)
                    )
                )
            }
        }

        return list
    }
}
