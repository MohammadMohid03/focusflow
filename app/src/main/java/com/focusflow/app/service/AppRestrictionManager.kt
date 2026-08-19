package com.focusflow.app.service

import com.focusflow.app.domain.model.RestrictableApp
import com.focusflow.app.domain.model.RestrictionCapability
import com.focusflow.app.domain.model.RestrictionResult

interface AppRestrictionManager {

    suspend fun getAvailableApps(): List<RestrictableApp>

    suspend fun enableRestriction(
        apps: List<String>,
        reason: String
    ): RestrictionResult

    suspend fun disableRestriction(
        apps: List<String>
    ): RestrictionResult

    suspend fun isRestrictionActive(packageName: String): Boolean

    suspend fun checkCapability(): RestrictionCapability

    suspend fun hasRequiredPermission(): Boolean

    suspend fun hasUsagePermission(): Boolean

    suspend fun hasOverlayPermission(): Boolean

    suspend fun requestPermissionSetup()

    suspend fun requestOverlayPermission()
}
