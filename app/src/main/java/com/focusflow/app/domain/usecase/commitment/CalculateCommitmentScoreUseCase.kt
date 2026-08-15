package com.focusflow.app.domain.usecase.commitment

import com.focusflow.app.domain.model.Commitment
import com.focusflow.app.domain.model.CommitmentScore
import com.focusflow.app.domain.model.CommitmentStatus
import javax.inject.Inject

class CalculateCommitmentScoreUseCase @Inject constructor() {
    operator fun invoke(commitments: List<Commitment>): CommitmentScore {
        if (commitments.isEmpty()) return CommitmentScore()

        val total = commitments.size
        val completed = commitments.count { it.status == CommitmentStatus.COMPLETED }
        val missed = commitments.count { it.status == CommitmentStatus.MISSED || it.status == CommitmentStatus.RESTRICTED }
        val cancelled = commitments.count { it.status == CommitmentStatus.CANCELLED }
        val recovered = commitments.count { it.status == CommitmentStatus.RESTORED }

        val rawScore = ((completed * 10 + recovered * 7 - missed * 8 - cancelled * 3).toFloat() / (total * 10).toFloat() * 100).toInt()
        val finalScore = rawScore.coerceIn(0, 100)

        return CommitmentScore(
            totalCommitments = total,
            completedCommitments = completed,
            missedCommitments = missed,
            cancelledCommitments = cancelled,
            recoveredCommitments = recovered,
            currentStreak = completed,
            longestStreak = completed,
            score = finalScore
        )
    }
}
