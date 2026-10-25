package com.focusflow.app.presentation.analytics

import com.focusflow.app.domain.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class AnalyticsAggregationTest {
    private val today = LocalDate.of(2026, 10, 19)
    private val zone = ZoneId.of("Asia/Karachi")
    private fun time(day: LocalDate) = day.atTime(9, 0).atZone(zone).toInstant().toEpochMilli()
    private fun session(day: LocalDate, minutes: Int?) = FocusSession(startTime = time(day), plannedDurationMinutes = 25, actualDurationMinutes = minutes)

    @Test fun `switching periods changes totals and chart buckets`() {
        val records = AnalyticsRecords(listOf(session(today, 10), session(today.minusDays(10), 20), session(today.minusMonths(2), 30)), emptyList(), emptyList(), emptyList())
        val week = buildAnalytics(records, AnalyticsPeriod.WEEK, today, zone)
        val month = buildAnalytics(records, AnalyticsPeriod.MONTH, today, zone)
        val year = buildAnalytics(records, AnalyticsPeriod.YEAR, today, zone)
        assertEquals(10, week.totalFocusTime)
        assertEquals(30, month.totalFocusTime)
        assertEquals(60, year.totalFocusTime)
        assertEquals(week.totalFocusTime, week.chartMinutes.sum())
        assertEquals(month.totalFocusTime, month.chartMinutes.sum())
        assertEquals(12, year.chartLabels.size)
    }

    @Test fun `completion uses completion date even for tasks created before period`() {
        val task = Task(title = "Old task", createdAt = time(today.minusMonths(1)), completedAt = time(today), isCompleted = true)
        val records = AnalyticsRecords(emptyList(), listOf(task, task.copy(id = "old", completedAt = time(today.minusDays(10)))), emptyList(), emptyList())
        val result = buildAnalytics(records, AnalyticsPeriod.WEEK, today, zone)
        assertEquals(1, result.tasksCompleted)
        assertEquals(1f, result.completionRate)
    }

    @Test fun `empty records show zero bars and an honest insight`() {
        val result = buildAnalytics(AnalyticsRecords(listOf(session(today, null)), emptyList(), emptyList(), emptyList()), AnalyticsPeriod.WEEK, today, zone)
        assertTrue(result.chartMinutes.all { it == 0 })
        assertEquals(0, result.commitmentScore)
        assertTrue(result.insight.startsWith("Complete a focus session"))
    }
}
