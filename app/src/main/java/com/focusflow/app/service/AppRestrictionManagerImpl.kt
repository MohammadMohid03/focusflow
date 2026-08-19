package com.focusflow.app.service

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import com.focusflow.app.domain.model.RestrictableApp
import com.focusflow.app.domain.model.RestrictionCapability
import com.focusflow.app.domain.model.RestrictionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRestrictionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppRestrictionManager {

    private val restrictedApps = mutableSetOf<String>()
    private val restrictionReasons = mutableMapOf<String, String>()

    override suspend fun getAvailableApps(): List<RestrictableApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        installedApps
            .filter { app ->
                val isSystemApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val isOurApp = app.packageName == context.packageName
                val hasLaunchIntent = pm.getLaunchIntentForPackage(app.packageName) != null
                !isOurApp && hasLaunchIntent && !isSystemApp
            }
            .map { app ->
                RestrictableApp(
                    packageName = app.packageName,
                    appName = pm.getApplicationLabel(app).toString(),
                    icon = try { pm.getApplicationIcon(app) } catch (e: Exception) { null },
                    isSelected = restrictedApps.contains(app.packageName),
                    isSystemApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }
            .sortedBy { it.appName.lowercase() }
    }

    override suspend fun enableRestriction(
        apps: List<String>,
        reason: String
    ): RestrictionResult = withContext(Dispatchers.IO) {
        return@withContext try {
            restrictedApps.addAll(apps)
            apps.forEach { restrictionReasons[it] = reason }

            if (apps.isNotEmpty()) {
                AppBlockerService.start(context, ArrayList(restrictedApps))
            }

            val capability = checkCapability()
            RestrictionResult(
                success = true,
                message = "Restrictions activated for ${apps.size} apps",
                capability = capability
            )
        } catch (e: Exception) {
            RestrictionResult(
                success = false,
                message = e.message ?: "Failed to enable restrictions",
                capability = RestrictionCapability.ERROR
            )
        }
    }

    override suspend fun disableRestriction(
        apps: List<String>
    ): RestrictionResult = withContext(Dispatchers.IO) {
        return@withContext try {
            restrictedApps.removeAll(apps.toSet())
            apps.forEach { restrictionReasons.remove(it) }

            if (restrictedApps.isEmpty()) {
                AppBlockerService.stop(context)
            } else {
                AppBlockerService.start(context, ArrayList(restrictedApps))
            }

            RestrictionResult(
                success = true,
                message = "Restrictions removed for ${apps.size} apps"
            )
        } catch (e: Exception) {
            RestrictionResult(
                success = false,
                message = e.message ?: "Failed to disable restrictions",
                capability = RestrictionCapability.ERROR
            )
        }
    }

    override suspend fun isRestrictionActive(packageName: String): Boolean {
        return restrictedApps.contains(packageName)
    }

    override suspend fun checkCapability(): RestrictionCapability {
        return try {
            if (!hasRequiredPermission()) {
                RestrictionCapability.REQUIRES_PERMISSION
            } else {
                RestrictionCapability.SUPPORTED
            }
        } catch (e: Exception) {
            RestrictionCapability.ERROR
        }
    }

    override suspend fun hasRequiredPermission(): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun requestPermissionSetup() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
