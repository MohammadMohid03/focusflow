package com.focusflow.app.service

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
        const val CHANNEL_ALERT_ID = "focusflow_lock_alert_channel"
        const val NOTIFICATION_ID = 9001
        const val NOTIFICATION_ALERT_ID = 9002
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
        createNotificationChannels()
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
                    val activeApps = appRestrictionManager.getActiveRestrictedApps()
                    if (activeApps.isEmpty()) {
                        stopMonitoring()
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                        break
                    }

                    val foregroundPackage = getForegroundPackage(usageStatsManager)
                    if (foregroundPackage != null && foregroundPackage != packageName) {
                        if (activeApps.contains(foregroundPackage)) {
                            val now = System.currentTimeMillis()
                            // Throttle repeated triggers within 1.2s
                            if (foregroundPackage != lastBlockedPackage || now - lastBlockTime > 1200) {
                                lastBlockedPackage = foregroundPackage
                                lastBlockTime = now
                                triggerAppLock(foregroundPackage)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Non-fatal
                }
                delay(500) // Poll every 500ms
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
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                lastForegroundApp = event.packageName
            }
        }
        return lastForegroundApp
    }

    private fun triggerAppLock(blockedPackage: String) {
        val pm = packageManager
        val appName = try {
            val appInfo = pm.getApplicationInfo(blockedPackage, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            blockedPackage
        }

        val lockIntent = Intent(this, FocusBlockActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(FocusBlockActivity.EXTRA_BLOCKED_APP_NAME, appName)
            putExtra(FocusBlockActivity.EXTRA_BLOCKED_PACKAGE, blockedPackage)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            blockedPackage.hashCode(),
            lockIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 1. Send High-Priority FullScreen Alert Notification (Supported on all Android versions)
        val alertNotification = NotificationCompat.Builder(this, CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_splash_logo)
            .setContentTitle("🔒 App Locked by FocusFlow")
            .setContentText("$appName is restricted during your focus commitment.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ALERT_ID, alertNotification)
        } catch (e: Exception) {}

        // 2. Launch Activity directly with Background Activity Launch permission options
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val options = ActivityOptions.makeBasic().apply {
                    pendingIntentBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
                }.toBundle()
                pendingIntent.send(this, 0, null, null, null, null, options)
            } else {
                pendingIntent.send()
            }
        } catch (e: Exception) {
            try {
                startActivity(lockIntent)
            } catch (e2: Exception) {}
        }
    }

    private fun stopMonitoring() {
        isMonitoring = false
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val monitorChannel = NotificationChannel(
                CHANNEL_ID,
                "Focus Lock Monitoring",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors app restrictions during focus commitments"
                setShowBadge(false)
            }

            val alertChannel = NotificationChannel(
                CHANNEL_ALERT_ID,
                "Focus Lock Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Immediate alerts and lock screen when restricted apps are opened"
                setBypassDnd(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(monitorChannel)
            manager.createNotificationChannel(alertChannel)
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
