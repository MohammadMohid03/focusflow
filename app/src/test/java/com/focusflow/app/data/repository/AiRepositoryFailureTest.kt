package com.focusflow.app.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference
import com.google.firebase.functions.HttpsCallableResult
import io.mockk.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class AiRepositoryFailureTest {
    private val functions = mockk<FirebaseFunctions>()
    private val callable = mockk<HttpsCallableReference>()
    private val repository = AiRepositoryImpl(functions, Json { ignoreUnknownKeys = true })

    init { every { functions.getHttpsCallable("generateAi") } returns callable }

    @Test fun `provider failure is not turned into a successful canned plan`() = runBlocking {
        val error = IllegalStateException("AI unavailable")
        every { callable.call(any()) } returns Tasks.forException(error)
        val result = repository.breakdownGoalOrTask("Learn Kotlin")
        assertTrue(result.isFailure)
        assertSame(error, result.exceptionOrNull())
    }

    @Test fun `malformed schedule reports failure instead of fixed example sessions`() = runBlocking {
        val response = mockk<HttpsCallableResult>()
        every { response.getData() } returns mapOf("content" to "not JSON")
        every { callable.call(any()) } returns Tasks.forResult(response)
        assertTrue(repository.generateDailySchedule(listOf("Review"), 2).isFailure)
    }

    @Test fun `cancellation propagates instead of becoming a response`() {
        every { callable.call(any()) } returns Tasks.forCanceled()
        assertThrows(CancellationException::class.java) {
            runBlocking { repository.sendMessage(emptyList(), "Hello") }
        }
    }
}
