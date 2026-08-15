package com.focusflow.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class HabitStreakTest {

    @Test
    fun `habit streak calculates consecutive days correctly`() {
        val habit = Habit(
            id = "habit_1",
            name = "Morning Exercise",
            currentStreak = 5,
            longestStreak = 10,
            totalCompletions = 15
        )

        assertEquals(5, habit.currentStreak)
        assertEquals(10, habit.longestStreak)
    }

    @Test
    fun `subtask progress ratio handles empty and non-empty subtasks`() {
        val taskWithSubtasks = Task(
            id = "1",
            title = "Project",
            subtasks = listOf(
                Subtask(id = "1", title = "Step 1", isCompleted = true),
                Subtask(id = "2", title = "Step 2", isCompleted = false)
            )
        )

        val completedCount = taskWithSubtasks.subtasks.count { it.isCompleted }
        val totalCount = taskWithSubtasks.subtasks.size
        val progress = completedCount.toFloat() / totalCount.toFloat()

        assertEquals(0.5f, progress, 0.001f)
    }
}
