package com.focusflow.app.data.repository

import com.focusflow.app.data.local.datastore.UserPreferencesDataStore
import com.focusflow.app.domain.model.ThemeMode
import com.focusflow.app.domain.model.UserPreferences
import com.focusflow.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : UserPreferencesRepository {

    override fun getUserPreferences(): Flow<UserPreferences> {
        return dataStore.getUserPreferences()
    }

    override fun getThemeMode(): Flow<ThemeMode> {
        return dataStore.getThemeMode()
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.setThemeMode(mode)
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return dataStore.isOnboardingCompleted()
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.setOnboardingCompleted(completed)
    }
}
