package com.focusflow.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.focusflow.app.MainActivity
import com.focusflow.app.R
import com.focusflow.app.presentation.commitment.FocusBlockActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class AppBlockerService : Service() {

    @Inject
    lateinit var appRestrictionManager: AppRestrictionManager

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var isMonitoring = false
    private var lastBlockedPackage: String? = null
    private var lastBlockTime: Long = 0

    companion object {
        const val CHANNEL_ID = "focusflow_blocker_channel"
        const val NOTIFICATION_ID = 9001
        const val ACTION_START = "com.focusflow.app.action.START_BLOCKER"
        const val ACTION_STOP = "com.focusflow.app.action.STOP_BLOCKER"
        const val EXTRA_RESTRICTED_APPS = "extra_restricted_apps"

        fun start(context: Context, apps: ArrayList<String>) {
            val intent = Intent(context, AppBlockerService::class.java).apply {
                action = ACTION_START
                putStringArrayListExtra(EXTRA_RESTRICTED_APPS, apps)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, AppBlockerService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopMonitoring()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val apps = intent?.getStringArrayListExtra(EXTRA_RESTRICTED_APPS) ?: arrayListOf()
        startForeground(NOTIFICATION_ID, buildForegroundNotification(apps.size))
        startMonitoring()

        return START_STICKY
    }

    private fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        serviceScope.launch {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            while (isActive && isMonitoring) {
                try {
                    val foregroundPackage = getForegroundPackage(usageStatsManager)
                    if (foregroundPackage != null && foregroundPackage != packageName) {
                        if (appRestrictionManager.isRestrictionActive(foregroundPackage)) {
                            val now = System.currentTimeMillis()
                            // Avoid spamming launch within 1.5 seconds for the same package
                            if (foregroundPackage != lastBlockedPackage || now - lastBlockTime > 1500) {
                                lastBlockedPackage = foregroundPackage
                                lastBlockTime = now
                                launchBlockScreen(foregroundPackage)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Ignore transient exceptions
                }
                delay(600) // Poll every 600ms for fast, responsive blocking
            }
        }
    }

    private fun getForegroundPackage(usageStatsManager: UsageStatsManager?): String? {
        if (usageStatsManager == null) return null
        val now = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(now - 3000, now)
        val event = UsageEvents.Event()
        var lastForegroundApp: String? = null

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                lastForegroundApp = event.packageName
            }
        }
        return lastForegroundApp
    }

    private fun launchBlockScreen(blockedPackage: String) {
        val pm = packageManager
        val appName = try {
            val appInfo = pm.getApplicationInfo(blockedPackage, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            blockedPackage
        }

        val intent = Intent(this, FocusBlockActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(FocusBlockActivity.EXTRA_BLOCKED_APP_NAME, appName)
            putExtra(FocusBlockActivity.EXTRA_BLOCKED_PACKAGE, blockedPackage)
        }
        startActivity(intent)
    }

    private fun stopMonitoring() {
        isMonitoring = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Lock Monitoring",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors app restrictions during focus commitments"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(appCount: Int): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_splash_logo)
            .setContentTitle("Commitment Lock Active 🔒")
            .setContentText("FocusFlow is monitoring distractions ($appCount apps restricted)")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        stopMonitoring()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
