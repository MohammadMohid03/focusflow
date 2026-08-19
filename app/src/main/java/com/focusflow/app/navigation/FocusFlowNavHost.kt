package com.focusflow.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.focusflow.app.presentation.components.BottomNavItemData
import com.focusflow.app.presentation.components.FocusFlowBottomNav
import com.focusflow.app.presentation.ai.AiChatScreen
import com.focusflow.app.presentation.ai.AiTaskBreakdownScreen
import com.focusflow.app.presentation.analytics.AnalyticsScreen
import com.focusflow.app.presentation.auth.ForgotPasswordScreen
import com.focusflow.app.presentation.auth.LoginScreen
import com.focusflow.app.presentation.auth.RegisterScreen
import com.focusflow.app.presentation.calendar.CalendarScreen
import com.focusflow.app.presentation.commitment.AppSelectionScreen
import com.focusflow.app.presentation.commitment.CommitmentConfigScreen
import com.focusflow.app.presentation.commitment.CommitmentHistoryScreen
import com.focusflow.app.presentation.commitment.CommitmentMissedScreen
import com.focusflow.app.presentation.commitment.CommitmentRecoveryScreen
import com.focusflow.app.presentation.commitment.CommitmentReviewScreen
import com.focusflow.app.presentation.focus.FocusHistoryScreen
import com.focusflow.app.presentation.focus.FocusScreen
import com.focusflow.app.presentation.focus.FocusSessionScreen
import com.focusflow.app.presentation.goals.CreateGoalScreen
import com.focusflow.app.presentation.goals.GoalDetailScreen
import com.focusflow.app.presentation.goals.GoalsScreen
import com.focusflow.app.presentation.habits.CreateHabitScreen
import com.focusflow.app.presentation.habits.HabitDetailScreen
import com.focusflow.app.presentation.habits.HabitsScreen
import com.focusflow.app.presentation.home.HomeScreen
import com.focusflow.app.presentation.onboarding.OnboardingScreen
import com.focusflow.app.presentation.planner.AiPlannerScreen
import com.focusflow.app.presentation.planner.PlannerScreen
import com.focusflow.app.presentation.profile.ProfileScreen
import com.focusflow.app.presentation.settings.AboutScreen
import com.focusflow.app.presentation.settings.AppearanceScreen
import com.focusflow.app.presentation.settings.PrivacyPolicyScreen
import com.focusflow.app.presentation.settings.SettingsScreen
import com.focusflow.app.presentation.settings.TermsScreen
import com.focusflow.app.presentation.splash.SplashScreen
import com.focusflow.app.presentation.tasks.CreateTaskScreen
import com.focusflow.app.presentation.tasks.TaskDetailScreen
import com.focusflow.app.presentation.tasks.TasksScreen

@Composable
fun FocusFlowNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = BottomNavItem.entries
    
    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Planner.route,
        Screen.AiChat.route,
        Screen.Profile.route,
        Screen.Tasks.route,
        Screen.Focus.route,
        Screen.Analytics.route
    )
    
    val showBottomBar = currentDestination?.route in bottomBarRoutes

    val navItems = remember {
        BottomNavItem.entries.map { 
            BottomNavItemData(it.route, it.icon, it.selectedIcon, it.label) 
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                FocusFlowBottomNav(
                    items = navItems,
                    currentRoute = currentDestination?.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
            }
        ) {
            // Splash & Auth
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onNavigateToPreferences = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToForgot = { navController.navigate(Screen.ForgotPassword.route) }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Dashboard & Tasks
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                    onNavigateToCreateTask = { navController.navigate(Screen.CreateTask.route) },
                    onNavigateToPlanner = { navController.navigate(Screen.Planner.route) },
                    onNavigateToAiChat = { navController.navigate(Screen.AiChat.route) },
                    onNavigateToFocus = { navController.navigate(Screen.Focus.route) }
                )
            }

            composable(Screen.Tasks.route) {
                TasksScreen(
                    onNavigateToCreateTask = { navController.navigate(Screen.CreateTask.route) },
                    onNavigateToTaskDetail = { taskId -> navController.navigate("task_detail/$taskId") }
                )
            }

            composable(Screen.CreateTask.route) {
                CreateTaskScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCommitment = { navController.navigate("commitment_config/new") }
                )
            }

            composable(
                route = Screen.TaskDetail.route,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                TaskDetailScreen(
                    taskId = taskId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Goals
            composable(Screen.Goals.route) {
                GoalsScreen(
                    onNavigateToCreateGoal = { navController.navigate(Screen.CreateGoal.route) },
                    onNavigateToGoalDetail = { goalId -> navController.navigate("goal_detail/$goalId") }
                )
            }

            composable(Screen.CreateGoal.route) {
                CreateGoalScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.GoalDetail.route,
                arguments = listOf(navArgument("goalId") { type = NavType.StringType })
            ) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
                GoalDetailScreen(
                    goalId = goalId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Planner
            composable(Screen.Planner.route) {
                PlannerScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.AiPlanner.route) {
                AiPlannerScreen(
                    viewModel = hiltViewModel()
                )
            }
            
            // AI Chat
            composable(Screen.AiChat.route) {
                AiChatScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.AiTaskBreakdown.route) {
                AiTaskBreakdownScreen(
                    viewModel = hiltViewModel()
                )
            }

            // Focus Mode
            composable(Screen.Focus.route) {
                FocusScreen(
                    onNavigateToSession = { navController.navigate(Screen.FocusSession.route) }
                )
            }

            composable(
                route = Screen.FocusSession.route,
                arguments = listOf(navArgument("taskId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) {
                FocusSessionScreen(
                    onSessionEnd = { navController.popBackStack() }
                )
            }

            composable(Screen.FocusHistory.route) {
                FocusHistoryScreen()
            }

            // Habits
            composable(Screen.Habits.route) {
                HabitsScreen(
                    onCreateHabit = { navController.navigate(Screen.CreateHabit.route) },
                    onHabitClick = { habitId -> navController.navigate("habit_detail/$habitId") }
                )
            }

            composable(Screen.CreateHabit.route) {
                CreateHabitScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.HabitDetail.route,
                arguments = listOf(navArgument("habitId") { type = NavType.StringType })
            ) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
                HabitDetailScreen(
                    habitId = habitId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Analytics
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }

            // Calendar
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }

            // Commitment Lock Flow
            composable(Screen.CommitmentHistory.route) {
                CommitmentHistoryScreen(
                    viewModel = hiltViewModel()
                )
            }

            composable(
                route = Screen.CommitmentConfig.route,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                CommitmentConfigScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateNext = { navController.navigate("app_selection/$taskId") },
                    viewModel = hiltViewModel()
                )
            }

            composable(
                route = Screen.AppSelection.route,
                arguments = listOf(navArgument("commitmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val commitmentId = backStackEntry.arguments?.getString("commitmentId") ?: ""
                AppSelectionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateNext = { navController.navigate("commitment_review/$commitmentId") },
                    viewModel = hiltViewModel()
                )
            }

            composable(
                route = Screen.CommitmentReview.route,
                arguments = listOf(navArgument("commitmentId") { type = NavType.StringType })
            ) {
                CommitmentReviewScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onActivate = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    viewModel = hiltViewModel()
                )
            }

            composable(
                route = Screen.CommitmentMissed.route,
                arguments = listOf(navArgument("commitmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val commitmentId = backStackEntry.arguments?.getString("commitmentId") ?: ""
                CommitmentMissedScreen(
                    onStartRecovery = { navController.navigate("commitment_recovery/$commitmentId") },
                    onCancel = { navController.navigate(Screen.Home.route) },
                    viewModel = hiltViewModel()
                )
            }

            composable(
                route = Screen.CommitmentRecovery.route,
                arguments = listOf(navArgument("commitmentId") { type = NavType.StringType })
            ) {
                CommitmentRecoveryScreen(
                    onStartFocus = { navController.navigate(Screen.Focus.route) },
                    onCancel = { navController.navigate(Screen.Home.route) },
                    viewModel = hiltViewModel()
                )
            }

            // Settings & Profile
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToPrivacy = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onNavigateToTerms = { navController.navigate(Screen.Terms.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Appearance.route) {
                AppearanceScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Terms.route) {
                TermsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
