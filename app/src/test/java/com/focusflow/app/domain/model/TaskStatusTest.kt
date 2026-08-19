package com.focusflow.app.domain.model

import org.junit.Assert.*
import org.junit.Test

class TaskStatusTest {

    @Test
    fun `task with past due date and not completed is marked overdue`() {
        val pastDue = System.currentTimeMillis() - 3600000 // 1 hour ago
        val task = Task(
            title = "Complete Math Assignment",
            dueDate = pastDue,
            isCompleted = false
        )

        assertTrue(task.isOverdue)
        assertEquals(TaskStatus.OVERDUE, task.derivedStatus)
    }

    @Test
    fun `completed task is marked COMPLETED even if due date is in the past`() {
        val pastDue = System.currentTimeMillis() - 3600000
        val task = Task(
            title = "Finished Project",
            dueDate = pastDue,
            isCompleted = true,
            completedAt = System.currentTimeMillis()
        )

        assertFalse(task.isOverdue)
        assertEquals(TaskStatus.COMPLETED, task.derivedStatus)
    }

    @Test
    fun `future task is marked TODO`() {
        val futureDue = System.currentTimeMillis() + 86400000 // tomorrow
        val task = Task(
            title = "Physics Lab",
            dueDate = futureDue,
            isCompleted = false
        )

        assertFalse(task.isOverdue)
        assertEquals(TaskStatus.TODO, task.derivedStatus)
    }
}
