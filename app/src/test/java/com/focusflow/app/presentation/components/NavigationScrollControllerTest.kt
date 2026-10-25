package com.focusflow.app.presentation.components

import org.junit.Assert.*
import org.junit.Test

class NavigationScrollControllerTest {
    @Test fun `downward content scroll compacts and upward scroll expands`() {
        val controller = NavigationScrollController(32f, 16f)
        assertFalse(controller.onScroll(-20f))
        assertTrue(controller.onScroll(-15f))
        assertTrue(controller.onScroll(10f))
        assertFalse(controller.onScroll(7f))
    }

    @Test fun `direction changes discard accumulated jitter`() {
        val controller = NavigationScrollController(32f, 16f)
        repeat(20) { controller.onScroll(-3f); controller.onScroll(3f) }
        assertFalse(controller.isCompact)
        assertFalse(controller.onScroll(0f))
        assertFalse(controller.onScroll(Float.NaN))
    }

    @Test fun `destination change resets previous scroll history`() {
        val controller = NavigationScrollController(32f, 16f)
        assertTrue(controller.onScroll(-100f))
        controller.reset()
        assertFalse(controller.isCompact)
        assertFalse(controller.onScroll(-10f))
    }
}
