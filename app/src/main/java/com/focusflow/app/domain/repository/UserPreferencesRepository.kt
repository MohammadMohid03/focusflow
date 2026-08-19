package com.focusflow.app.domain.repository

import com.focusflow.app.domain.model.ThemeMode
import com.focusflow.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
    fun getUserName(): Flow<String>
    suspend fun setUserName(name: String)
    fun getUserEmail(): Flow<String>
    suspend fun setUserEmail(email: String)
    fun getUserPreferences(): Flow<UserPreferences>
}
