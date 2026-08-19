package com.focusflow.app.service

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import com.focusflow.app.data.local.dao.CommitmentDao
import com.focusflow.app.data.local.mapper.toDomain
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
    @ApplicationContext private val context: Context,
    private val commitmentDao: CommitmentDao
) : AppRestrictionManager {

    private val inMemoryRestrictedApps = mutableSetOf<String>()
    private val restrictionReasons = mutableMapOf<String, String>()

    override suspend fun getAvailableApps(): List<RestrictableApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val activeApps = getActiveRestrictedApps()

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
                    isSelected = activeApps.contains(app.packageName),
                    isSystemApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }
            .sortedBy { it.appName.lowercase() }
    }

    override suspend fun getActiveRestrictedApps(): Set<String> = withContext(Dispatchers.IO) {
        val dbActiveApps = try {
            commitmentDao.getAllActiveCommitmentsSync()
                .map { it.toDomain() }
                .flatMap { it.selectedAppPackages }
                .toSet()
        } catch (e: Exception) {
            emptySet()
        }
        return@withContext (inMemoryRestrictedApps + dbActiveApps)
    }

    override suspend fun enableRestriction(
        apps: List<String>,
        reason: String
    ): RestrictionResult = withContext(Dispatchers.IO) {
        return@withContext try {
            inMemoryRestrictedApps.addAll(apps)
            apps.forEach { restrictionReasons[it] = reason }

            val totalActive = getActiveRestrictedApps()
            if (totalActive.isNotEmpty()) {
                AppBlockerService.start(context, ArrayList(totalActive))
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
            inMemoryRestrictedApps.removeAll(apps.toSet())
            apps.forEach { restrictionReasons.remove(it) }

            val remainingActive = getActiveRestrictedApps()
            if (remainingActive.isEmpty()) {
                AppBlockerService.stop(context)
            } else {
                AppBlockerService.start(context, ArrayList(remainingActive))
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

    override suspend fun isRestrictionActive(packageName: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext getActiveRestrictedApps().contains(packageName)
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
        return hasUsagePermission() && hasOverlayPermission()
    }

    override suspend fun hasUsagePermission(): Boolean {
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

    override suspend fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    override suspend fun requestPermissionSetup() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    override suspend fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
