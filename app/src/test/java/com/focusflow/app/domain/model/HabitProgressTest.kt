package com.focusflow.app.domain.model

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class HabitProgressTest {
    private val today = LocalDate.of(2026, 10, 19)

    @Test fun `duplicate same-day completions do not increase progress`() {
        val result = Habit(name = "Read").withProgress(listOf(today, today, today.minusDays(1)), today)
        assertEquals(2, result.currentStreak)
        assertEquals(2, result.totalCompletions)
    }

    @Test fun `missed days reset current streak but preserve longest streak`() {
        val result = Habit(name = "Read").withProgress(listOf(today.minusDays(3), today.minusDays(4)), today)
        assertEquals(0, result.currentStreak)
        assertEquals(2, result.longestStreak)
    }

    @Test fun `incomplete today preserves yesterday streak until today is missed`() {
        assertEquals(2, Habit(name = "Read").withProgress(listOf(today.minusDays(1), today.minusDays(2)), today).currentStreak)
    }

    @Test fun `custom weekdays skip weekends without breaking the streak`() {
        val habit = Habit(name = "Work", frequency = HabitFrequency.CUSTOM, customDays = listOf(0, 1, 2, 3, 4))
        assertEquals(2, habit.withProgress(listOf(today, today.minusDays(3)), today).currentStreak)
        assertFalse(habit.isScheduledOn(today.minusDays(1)))
    }

    @Test fun `weekly streak counts weeks rather than taps or calendar days`() {
        val habit = Habit(name = "Review", frequency = HabitFrequency.WEEKLY)
        val result = habit.withProgress(listOf(today, today.minusDays(1), today.minusDays(2), today.minusDays(8)), today)
        assertEquals(3, result.currentStreak)
        assertEquals(3, result.totalCompletions)
    }
}
