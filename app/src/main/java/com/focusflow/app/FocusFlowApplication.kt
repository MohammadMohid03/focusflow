package com.focusflow.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FocusFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
