package com.example.basic

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class DaySchedule(val date: LocalDate, val events: List<ClassEntry>)

object PlannerRepository {
    private fun dayKey(day: DayOfWeek): String {
        return day.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    fun getClasses(date: LocalDate): List<ClassEntry> {
        val key = dayKey(date.dayOfWeek)
        return WEEKLY_SCHEDULE[key].orEmpty()
    }

    fun weekSchedule(start: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)): List<DaySchedule> {
        return WEEKLY_SCHEDULE.entries.mapIndexed { index, entry ->
            DaySchedule(start.plusDays(index.toLong()), entry.value)
        }
    }
}
