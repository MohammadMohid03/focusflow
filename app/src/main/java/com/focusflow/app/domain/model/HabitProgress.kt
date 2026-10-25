package com.focusflow.app.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

fun Habit.isScheduledOn(date: LocalDate): Boolean = frequency != HabitFrequency.CUSTOM ||
    customDays.isEmpty() || date.dayOfWeek.ordinal in customDays

/** Rebuild progress from distinct completion dates, including legacy duplicate rows. */
fun Habit.withProgress(completedDates: List<LocalDate>, today: LocalDate): Habit {
    fun key(date: LocalDate): LocalDate = if (frequency == HabitFrequency.WEEKLY)
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) else date
    fun previous(date: LocalDate): LocalDate {
        if (frequency == HabitFrequency.WEEKLY) return date.minusWeeks(1)
        var result = date.minusDays(1)
        while (!isScheduledOn(result)) result = result.minusDays(1)
        return result
    }
    val dates = completedDates.filter { !it.isAfter(today) && isScheduledOn(it) }.map(::key).toSortedSet()
    var longest = 0
    var run = 0
    var last: LocalDate? = null
    dates.forEach { date ->
        run = if (last == previous(date)) run + 1 else 1
        longest = maxOf(longest, run)
        last = date
    }
    var cursor = key(today)
    if (!isScheduledOn(today) || cursor !in dates) cursor = previous(cursor)
    var current = 0
    while (cursor in dates) { current++; cursor = previous(cursor) }
    return copy(currentStreak = current, longestStreak = longest, totalCompletions = dates.size)
}
