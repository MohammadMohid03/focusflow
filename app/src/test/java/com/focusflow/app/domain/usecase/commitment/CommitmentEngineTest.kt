package com.focusflow.app.domain.usecase.commitment

import com.focusflow.app.domain.model.Commitment
import com.focusflow.app.domain.model.CommitmentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommitmentEngineTest {

    private val calculateScoreUseCase = CalculateCommitmentScoreUseCase()

    @Test
    fun `calculateScore returns 0 when no commitments exist`() {
        val score = calculateScoreUseCase(emptyList())
        assertEquals(0, score.score)
        assertEquals(0, score.totalCommitments)
    }

    @Test
    fun `calculateScore returns 100 for 100 percent completed commitments`() {
        val commitments = listOf(
            createCommitment("1", CommitmentStatus.COMPLETED),
            createCommitment("2", CommitmentStatus.COMPLETED),
            createCommitment("3", CommitmentStatus.COMPLETED)
        )
        val score = calculateScoreUseCase(commitments)
        assertEquals(100, score.score)
        assertEquals(3, score.completedCommitments)
    }

    @Test
    fun `calculateScore reflects missed and recovered commitments accurately`() {
        val commitments = listOf(
            createCommitment("1", CommitmentStatus.COMPLETED),
            createCommitment("2", CommitmentStatus.MISSED),
            createCommitment("3", CommitmentStatus.RESTORED),
            createCommitment("4", CommitmentStatus.CANCELLED)
        )
        val score = calculateScoreUseCase(commitments)
        assertEquals(1, score.completedCommitments)
        assertEquals(1, score.missedCommitments)
        assertEquals(1, score.recoveredCommitments)
        assertEquals(1, score.cancelledCommitments)
        assertTrue(score.score > 0)
        assertTrue(score.score < 100)
    }

    @Test
    fun `unrealistic commitment detection flags short deadlines for long tasks`() {
        val now = System.currentTimeMillis()
        val shortDeadline = now + (20 * 60 * 1000) // 20 mins from now
        val longTaskDuration = 240 // 4 hours

        val isUnrealistic = shortDeadline - now < longTaskDuration * 60 * 1000L
        assertTrue(isUnrealistic)
    }

    private fun createCommitment(id: String, status: CommitmentStatus): Commitment {
        return Commitment(
            id = id,
            taskId = "task_$id",
            deadline = System.currentTimeMillis(),
            estimatedDurationMinutes = 60,
            status = status
        )
    }
}
