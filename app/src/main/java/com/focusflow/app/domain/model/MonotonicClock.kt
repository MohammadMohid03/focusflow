package com.focusflow.app.domain.model

import javax.inject.Inject

open class MonotonicClock @Inject constructor() {
    open fun nowMillis(): Long = System.nanoTime() / 1_000_000
}
