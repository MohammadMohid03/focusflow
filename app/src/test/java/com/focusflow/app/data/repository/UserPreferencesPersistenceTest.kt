package com.focusflow.app.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.focusflow.app.data.local.datastore.UserPreferencesDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files

class UserPreferencesPersistenceTest {
    @Test fun `notification and timer settings survive reopening datastore`() = runTest {
        val folder = Files.createTempDirectory("focusflow-settings").toFile()
        val file = folder.resolve("settings.preferences_pb")
        var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val first = UserPreferencesDataStore(PreferenceDataStoreFactory.create(scope = scope, produceFile = { file }))
            first.updateUserPreferences { it.copy(notificationsEnabled = false, taskReminders = false, defaultFocusDuration = 45, defaultBreakDuration = 8) }
            first.updateUserPreferences { it.copy(habitReminders = false) }
            scope.coroutineContext[Job]!!.cancelAndJoin()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            val second = UserPreferencesDataStore(PreferenceDataStoreFactory.create(scope = scope, produceFile = { file }))
            val saved = second.getUserPreferences().first()
            assertFalse(saved.notificationsEnabled)
            assertFalse(saved.taskReminders)
            assertFalse(saved.habitReminders)
            assertEquals(45, saved.defaultFocusDuration)
            assertEquals(8, saved.defaultBreakDuration)
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
            folder.deleteRecursively()
        }
    }
}
