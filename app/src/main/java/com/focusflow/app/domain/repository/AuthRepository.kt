package com.focusflow.app.domain.repository

import com.focusflow.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<UserProfile>
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<UserProfile>
    suspend fun signInWithGoogle(idToken: String): Result<UserProfile>
    suspend fun signOut()
    suspend fun resetPassword(email: String): Result<Unit>
    fun getCurrentUser(): UserProfile?
    fun isUserLoggedIn(): Boolean
    fun observeAuthState(): Flow<UserProfile?>
}
