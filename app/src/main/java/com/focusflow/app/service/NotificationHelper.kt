package com.focusflow.app.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.focusflow.app.MainActivity
import com.focusflow.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import com.focusflow.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import androidx.core.app.NotificationManagerCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: UserPreferencesRepository
) {
    companion object {
        const val CHANNEL_TASKS = "tasks_channel"
        const val CHANNEL_HABITS = "habits_channel"
        const val CHANNEL_FOCUS = "focus_channel"
        const val CHANNEL_COMMITMENTS = "commitments_channel"
        const val CHANNEL_DAILY = "daily_channel"

        private const val NOTIFICATION_ID_BASE_TASK = 1000
        private const val NOTIFICATION_ID_BASE_HABIT = 2000
        private const val NOTIFICATION_ID_BASE_FOCUS = 3000
        private const val NOTIFICATION_ID_BASE_COMMITMENT = 4000
        private const val NOTIFICATION_ID_DAILY = 5000
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                CHANNEL_TASKS,
                "Task Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for upcoming task deadlines"
            },
            NotificationChannel(
                CHANNEL_HABITS,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to complete your daily habits"
            },
            NotificationChannel(
                CHANNEL_FOCUS,
                "Focus Sessions",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Focus session updates and completion"
            },
            NotificationChannel(
                CHANNEL_COMMITMENTS,
                "Commitment Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Commitment deadline warnings and status updates"
            },
            NotificationChannel(
                CHANNEL_DAILY,
                "Daily Plan",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Your daily productivity plan"
            }
        )

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        channels.forEach { notificationManager.createNotificationChannel(it) }
    }

    private suspend fun isEnabled(channel: String): Boolean {
        val preferences = preferencesRepository.getUserPreferences().first()
        return preferences.notificationsEnabled && when (channel) {
            CHANNEL_TASKS -> preferences.taskReminders
            CHANNEL_HABITS -> preferences.habitReminders
            CHANNEL_COMMITMENTS -> preferences.commitmentReminders
            CHANNEL_FOCUS -> preferences.focusReminders
            CHANNEL_DAILY -> preferences.dailyPlanReminder
            else -> false
        }
    }

    private fun postNotification(id: Int, notification: android.app.Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (_: SecurityException) {
            // Permission may be revoked between checking and posting.
        }
    }

    private fun getMainActivityIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    suspend fun showTaskReminder(taskId: String, taskTitle: String, dueTime: String) {
        if (!isEnabled(CHANNEL_TASKS)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_TASKS)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Task Reminder")
            .setContentText("$taskTitle is due $dueTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getMainActivityIntent())
            .setAutoCancel(true)
            .build()

        postNotification(
            NOTIFICATION_ID_BASE_TASK + taskId.hashCode() % 1000,
            notification
        )
    }

    suspend fun showHabitReminder(habitId: String, habitName: String) {
        if (!isEnabled(CHANNEL_HABITS)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_HABITS)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Habit Reminder")
            .setContentText("Time to complete: $habitName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getMainActivityIntent())
            .setAutoCancel(true)
            .build()

        postNotification(
            NOTIFICATION_ID_BASE_HABIT + habitId.hashCode() % 1000,
            notification
        )
    }

    suspend fun showFocusSessionComplete(sessionDuration: Int) {
        if (!isEnabled(CHANNEL_FOCUS)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_FOCUS)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Session Complete!")
            .setContentText("Great work! You focused for $sessionDuration minutes.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(getMainActivityIntent())
            .setAutoCancel(true)
            .build()

        postNotification(
            NOTIFICATION_ID_BASE_FOCUS,
            notification
        )
    }

    suspend fun showCommitmentWarning(commitmentId: String, taskName: String, minutesRemaining: Int) {
        if (!isEnabled(CHANNEL_COMMITMENTS)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_COMMITMENTS)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("$minutesRemaining minutes left")
            .setContentText("Complete your commitment before Focus Lock activates: $taskName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getMainActivityIntent())
            .setAutoCancel(true)
            .build()

        postNotification(
            NOTIFICATION_ID_BASE_COMMITMENT + commitmentId.hashCode() % 1000,
            notification
        )
    }

    suspend fun showCommitmentMissed(commitmentId: String, taskName: String) {
        if (!isEnabled(CHANNEL_COMMITMENTS)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_COMMITMENTS)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Commitment Missed")
            .setContentText("Your selected consequence has been activated for: $taskName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getMainActivityIntent())
            .setAutoCancel(true)
            .build()

        postNotification(
            NOTIFICATION_ID_BASE_COMMITMENT + commitmentId.hashCode() % 1000 + 500,
            notification
        )
    }

    suspend fun showCommitmentCompleted(commitmentId: String, taskName: String) {
        if (!isEnabled(CHANNEL_COMMITMENTS)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_COMMITMENTS)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Commitment Completed! 🎉")
            .setContentText("Great work completing: $taskName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getMainActivityIntent())
            .setAutoCancel(true)
            .build()

        postNotification(
            NOTIFICATION_ID_BASE_COMMITMENT + commitmentId.hashCode() % 1000 + 999,
            notification
        )
    }

    suspend fun showCommitmentActivated(taskName: String, deadline: String) {
        if (!isEnabled(CHANNEL_COMMITMENTS)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_COMMITMENTS)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Commitment Activated")
            .setContentText("$taskName is due at $deadline")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getMainActivityIntent())
            .setAutoCancel(true)
            .build()

        postNotification(
            NOTIFICATION_ID_BASE_COMMITMENT + taskName.hashCode() % 1000 + 100,
            notification
        )
    }

    suspend fun showRecoveryAvailable(commitmentId: String, taskName: String, recoveryMinutes: Int) {
        if (!isEnabled(CHANNEL_COMMITMENTS)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_COMMITMENTS)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Recovery Available")
            .setContentText("Complete $recoveryMinutes minutes of focused work to restore access.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getMainActivityIntent())
            .setAutoCancel(true)
            .build()

        postNotification(
            NOTIFICATION_ID_BASE_COMMITMENT + commitmentId.hashCode() % 1000 + 200,
            notification
        )
    }

    suspend fun showDailyPlan(taskCount: Int, focusMinutes: Int) {
        if (!isEnabled(CHANNEL_DAILY)) return

        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Your Day Ahead")
            .setContentText("$taskCount tasks planned · $focusMinutes min focus time")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getMainActivityIntent())
            .setAutoCancel(true)
            .build()

        postNotification(NOTIFICATION_ID_DAILY, notification)
    }
}
