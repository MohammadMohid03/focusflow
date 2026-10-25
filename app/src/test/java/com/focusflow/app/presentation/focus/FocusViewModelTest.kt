package com.focusflow.app.presentation.focus

import androidx.lifecycle.ViewModelStore
import com.focusflow.app.domain.model.*
import com.focusflow.app.domain.repository.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class FocusViewModelTest {
    private val repository = mockk<FocusSessionRepository>()
    private val auth = mockk<AuthRepository>()
    private val preferences = mockk<UserPreferencesRepository>()
    private val saved = mutableListOf<FocusSession>()
    private val store = ViewModelStore()
    private var now = 0L
    private val clock = object : MonotonicClock() { override fun nowMillis() = now }
    private lateinit var vm: FocusViewModel

    @Before fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        val user = UserProfile(id = "owner")
        every { auth.getCurrentUser() } returns user
        every { auth.observeAuthState() } returns flowOf(user)
        every { repository.getAllSessions("owner") } returns flowOf(emptyList())
        coEvery { repository.insertSession(capture(saved)) } just Runs
        every { preferences.getUserPreferences() } returns flowOf(UserPreferences(defaultFocusDuration = 45, defaultBreakDuration = 7))
        vm = FocusViewModel(repository, auth, preferences, clock)
        store.put("focus", vm)
    }

    private fun regressionTest(block: suspend TestScope.() -> Unit) = runTest {
        try { block() } finally { store.clear(); runCurrent() }
    }

    @After fun cleanup() { store.clear(); Dispatchers.resetMain() }

    @Test fun `custom preference and selected preset survive starting the timer`() = regressionTest {
        runCurrent()
        assertEquals(45, vm.uiState.value.customDurationMinutes)
        vm.selectSessionType(FocusSessionType.POMODORO_50_10)
        vm.startSession()
        assertEquals(3000, vm.uiState.value.timeRemaining)
        now = 90_000
        vm.tick()
        assertEquals(2910, vm.uiState.value.timeRemaining)
        vm.endSession(); runCurrent()
        assertEquals(50, saved.single().plannedDurationMinutes)
        assertEquals(1, saved.single().actualDurationMinutes)
        assertFalse(saved.single().isCompleted)
    }

    @Test fun `immediate stop earns zero minutes and cannot save twice`() = regressionTest {
        runCurrent(); vm.startSession(); vm.endSession(); vm.endSession(); runCurrent()
        assertEquals(1, saved.size)
        assertEquals(0, saved.single().actualDurationMinutes)
        assertFalse(saved.single().isCompleted)
    }

    @Test fun `paused time is excluded and duplicate starts do not reset timer`() = regressionTest {
        runCurrent(); vm.startSession()
        now = 60_000; vm.pauseSession()
        now = 660_000; vm.tick()
        assertEquals(44 * 60, vm.uiState.value.timeRemaining)
        vm.resumeSession(); vm.startSession()
        now += 60_000; vm.endSession(); runCurrent()
        assertEquals(2, saved.single().actualDurationMinutes)
    }

    @Test fun `natural completion saves once then starts configured break`() = regressionTest {
        runCurrent(); vm.setCustomDuration(1); vm.startSession()
        now = 60_000; vm.tick(); runCurrent()
        assertTrue(saved.single().isCompleted)
        assertTrue(vm.uiState.value.isBreak)
        assertEquals(7 * 60, vm.uiState.value.timeRemaining)
        now += 7 * 60_000; vm.tick(); runCurrent()
        assertFalse(vm.uiState.value.isRunning)
        assertEquals(1, saved.size)
        vm.startSession()
        assertEquals(60, vm.uiState.value.timeRemaining)
    }
}
