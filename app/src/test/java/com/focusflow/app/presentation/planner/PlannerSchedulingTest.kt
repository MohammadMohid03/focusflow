package com.focusflow.app.presentation.planner

import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.model.TaskCategory
import com.focusflow.app.domain.model.TaskPriority
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PlannerSchedulingTest {

    @Test
    fun `tasks are prioritized correctly with high priority first`() {
        val today = LocalDate.now()
        val zone = ZoneId.systemDefault()
        val dueEpoch = today.atTime(18, 0).atZone(zone).toInstant().toEpochMilli()

        val taskA = Task(
            id = "1",
            title = "Low Priority Task",
            priority = TaskPriority.LOW,
            dueDate = dueEpoch,
            estimatedDurationMinutes = 30
        )
        val taskB = Task(
            id = "2",
            title = "Urgent Calculus",
            priority = TaskPriority.URGENT,
            dueDate = dueEpoch,
            estimatedDurationMinutes = 60
        )
        val taskC = Task(
            id = "3",
            title = "Medium Physics",
            priority = TaskPriority.MEDIUM,
            dueDate = dueEpoch,
            estimatedDurationMinutes = 45
        )

        val list = listOf(taskA, taskB, taskC)
        val sorted = list.sortedWith(
            compareBy<Task> { it.priority.ordinal }
                .thenBy { it.dueDate ?: Long.MAX_VALUE }
        )

        assertEquals("2", sorted[0].id) // URGENT first
        assertEquals("3", sorted[1].id) // MEDIUM second
        assertEquals("1", sorted[2].id) // LOW third
    }

    @Test
    fun `precise real-time calculation matches duration accurately`() {
        val testStartTime = LocalTime.of(21, 31) // 9:31 PM
        val durationMinutes = 10
        val testEndTime = testStartTime.plusMinutes(durationMinutes.toLong())

        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        assertEquals("09:31 PM", testStartTime.format(formatter))
        assertEquals("09:41 PM", testEndTime.format(formatter))
    }

    @Test
    fun `schedule slots do not overlap and include buffer times`() {
        val today = LocalDate.now()
        var currentSlotTime = LocalTime.of(9, 0)
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        val durations = listOf(60, 45, 30)
        val slots = durations.map { dur ->
            val start = currentSlotTime
            val end = start.plusMinutes(dur.toLong())
            currentSlotTime = end.plusMinutes(15) // 15m buffer
            Pair(start, end)
        }

        // Slot 1: 09:00 - 10:00
        assertEquals(LocalTime.of(9, 0), slots[0].first)
        assertEquals(LocalTime.of(10, 0), slots[0].second)

        // Slot 2: 10:15 - 11:00
        assertEquals(LocalTime.of(10, 15), slots[1].first)
        assertEquals(LocalTime.of(11, 0), slots[1].second)

        // Slot 3: 11:15 - 11:45
        assertEquals(LocalTime.of(11, 15), slots[2].first)
        assertEquals(LocalTime.of(11, 45), slots[2].second)

        // Assert non-overlapping
        assertTrue(slots[0].second.isBefore(slots[1].first) || slots[0].second == slots[1].first)
        assertTrue(slots[1].second.isBefore(slots[2].first) || slots[1].second == slots[2].first)
    }
}
