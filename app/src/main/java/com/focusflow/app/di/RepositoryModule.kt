package com.focusflow.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.focusflow.app.domain.repository.*
import com.focusflow.app.data.repository.*
import com.focusflow.app.service.AppRestrictionManager
import com.focusflow.app.service.AppRestrictionManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
    @Binds @Singleton abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository
    @Binds @Singleton abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository
    @Binds @Singleton abstract fun bindFocusSessionRepository(impl: FocusSessionRepositoryImpl): FocusSessionRepository
    @Binds @Singleton abstract fun bindCommitmentRepository(impl: CommitmentRepositoryImpl): CommitmentRepository
    @Binds @Singleton abstract fun bindPlannerRepository(impl: PlannerRepositoryImpl): PlannerRepository
    @Binds @Singleton abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
    @Binds @Singleton abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindAppRestrictionManager(impl: AppRestrictionManagerImpl): AppRestrictionManager
}

