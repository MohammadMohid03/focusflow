package com.focusflow.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    
    object Home : Screen("home")
    object Tasks : Screen("tasks")
    object CreateTask : Screen("create_task")
    data class TaskDetail(val taskId: String) : Screen("task_detail/$taskId") {
        companion object {
            const val route = "task_detail/{taskId}"
        }
    }
    data class EditTask(val taskId: String) : Screen("edit_task/$taskId") {
        companion object {
            const val route = "edit_task/{taskId}"
        }
    }
    
    object Goals : Screen("goals")
    object CreateGoal : Screen("create_goal")
    data class GoalDetail(val goalId: String) : Screen("goal_detail/$goalId") {
        companion object {
            const val route = "goal_detail/{goalId}"
        }
    }
    
    object Planner : Screen("planner")
    object AiPlanner : Screen("ai_planner")
    object AiChat : Screen("ai_chat")
    object AiTaskBreakdown : Screen("ai_task_breakdown")
    
    object Focus : Screen("focus")
    object FocusHistory : Screen("focus_history")
    data class FocusSession(val taskId: String? = null) : Screen(
        if (taskId != null) "focus_session?taskId=$taskId" else "focus_session"
    ) {
        companion object {
            const val route = "focus_session?taskId={taskId}"
        }
    }
    
    object Habits : Screen("habits")
    object CreateHabit : Screen("create_habit")
    data class HabitDetail(val habitId: String) : Screen("habit_detail/$habitId") {
        companion object {
            const val route = "habit_detail/{habitId}"
        }
    }
    
    object Calendar : Screen("calendar")
    
    object Analytics : Screen("analytics")
    object AnalyticsDetail : Screen("analytics_detail")
    
    object Commitment : Screen("commitment")
    object CommitmentHistory : Screen("commitment_history")
    
    data class CommitmentConfig(val taskId: String) : Screen("commitment_config/$taskId") {
        companion object {
            const val route = "commitment_config/{taskId}"
        }
    }
    data class CommitmentReview(val commitmentId: String) : Screen("commitment_review/$commitmentId") {
        companion object {
            const val route = "commitment_review/{commitmentId}"
        }
    }
    data class CommitmentMissed(val commitmentId: String) : Screen("commitment_missed/$commitmentId") {
        companion object {
            const val route = "commitment_missed/{commitmentId}"
        }
    }
    data class CommitmentRecovery(val commitmentId: String) : Screen("commitment_recovery/$commitmentId") {
        companion object {
            const val route = "commitment_recovery/{commitmentId}"
        }
    }
    data class AppSelection(val commitmentId: String) : Screen("app_selection/$commitmentId") {
        companion object {
            const val route = "app_selection/{commitmentId}"
        }
    }
    
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object NotificationSettings : Screen("notification_settings")
    object Appearance : Screen("appearance")
    object Account : Screen("account")
    object About : Screen("about")
    object PrivacyPolicy : Screen("privacy_policy")
    object Terms : Screen("terms")
}
