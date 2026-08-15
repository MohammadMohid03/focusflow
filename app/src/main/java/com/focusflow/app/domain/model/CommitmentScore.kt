package com.focusflow.app.domain.model

data class CommitmentScore(
    val totalCommitments: Int = 0,
    val completedCommitments: Int = 0,
    val missedCommitments: Int = 0,
    val cancelledCommitments: Int = 0,
    val recoveredCommitments: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val score: Int = 0 // 0-100
)
