package com.focusflow.app.presentation.components

/** Tracks actual content movement; horizontal swipes and tiny jitters do nothing. */
internal class NavigationScrollController(private val collapseDistance: Float, private val expandDistance: Float) {
    var isCompact: Boolean = false
        private set
    private var distance = 0f

    fun onScroll(deltaY: Float): Boolean {
        if (!deltaY.isFinite() || deltaY == 0f) return isCompact
        if ((deltaY > 0) != (distance > 0)) distance = 0f
        distance += deltaY
        if (!isCompact && distance <= -collapseDistance) {
            isCompact = true
            distance = 0f
        } else if (isCompact && distance >= expandDistance) {
            isCompact = false
            distance = 0f
        }
        return isCompact
    }

    fun reset() { isCompact = false; distance = 0f }
}
