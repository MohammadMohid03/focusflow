package com.focusflow.app.domain.model

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

fun observeToday() = flow {
    while (true) {
        emit(LocalDate.now())
        delay(30_000)
    }
}.distinctUntilChanged()
