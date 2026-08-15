package com.focusflow.app.domain.model

data class UserProfile(
    val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val primaryGoal: String = "",
    val productivityArea: String = "",
    val preferredWorkingTime: PreferredTime = PreferredTime.MORNING,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)
