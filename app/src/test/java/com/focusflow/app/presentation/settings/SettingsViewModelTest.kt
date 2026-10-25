package com.focusflow.app.presentation.settings

import androidx.lifecycle.ViewModelStore
import com.focusflow.app.domain.model.UserPreferences
import com.focusflow.app.domain.model.UserProfile
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.UserPreferencesRepository
import com.focusflow.app.service.AppRestrictionManager
import com.focusflow.app.service.UserDataManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val preferences = mockk<UserPreferencesRepository>(relaxed = true)
    private val auth = mockk<AuthRepository>(relaxed = true)
    private val restrictions = mockk<AppRestrictionManager>(relaxed = true)
    private val data = mockk<UserDataManager>(relaxed = true)
    private val store = ViewModelStore()
    private lateinit var vm: SettingsViewModel

    @Before fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        every { preferences.getUserPreferences() } returns flowOf(UserPreferences())
        every { preferences.getUserName() } returns flowOf("Owner")
        every { preferences.getUserEmail() } returns flowOf("owner@example.com")
        every { auth.getCurrentUser() } returns UserProfile(id = "owner")
        vm = SettingsViewModel(preferences, auth, restrictions, data)
        store.put("settings", vm)
    }
    private fun regressionTest(block: suspend TestScope.() -> Unit) = runTest {
        try { block() } finally { store.clear(); runCurrent() }
    }

    @After fun cleanup() { store.clear(); Dispatchers.resetMain() }

    @Test fun `failed remote deletion preserves local data and reports failure`() = regressionTest {
        coEvery { auth.deleteAccount() } returns Result.failure(IllegalStateException("Recent login required"))
        var completed = false
        vm.deleteAccount { completed = true }; runCurrent()
        coVerify(exactly = 0) { data.deleteLocalData(any()) }
        coVerify(exactly = 0) { auth.signOut() }
        assertFalse(completed)
        assertFalse(vm.uiState.value.isSignedOut)
        assertTrue(vm.uiState.value.message!!.contains("Recent login required"))
    }

    @Test fun `successful deletion cleans only captured owner and finishes once`() = regressionTest {
        coEvery { auth.deleteAccount() } returns Result.success(Unit)
        var completed = 0
        vm.deleteAccount { completed++ }
        vm.deleteAccount { completed++ }
        runCurrent()
        coVerify(exactly = 1) { auth.deleteAccount() }
        coVerify(exactly = 1) { data.deleteLocalData("owner") }
        coVerify { preferences.setUserName(""); preferences.setUserEmail("") }
        assertEquals(1, completed)
        assertTrue(vm.uiState.value.isSignedOut)
    }
}
