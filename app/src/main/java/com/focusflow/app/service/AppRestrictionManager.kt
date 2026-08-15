package com.focusflow.app.service

import com.focusflow.app.domain.model.RestrictableApp
import com.focusflow.app.domain.model.RestrictionCapability
import com.focusflow.app.domain.model.RestrictionResult

/**
 * Abstraction layer for app restriction functionality.
 * Isolates the commitment engine from specific Android APIs,
 * allowing the restriction mechanism to change without rewriting
 * the business logic.
 */
interface AppRestrictionManager {

    /**
     * Returns the list of installed apps that can potentially be restricted
     * under the supported Android mechanism.
     */
    suspend fun getAvailableApps(): List<RestrictableApp>

    /**
     * Enables restriction for the given app package names.
     * @param apps Package names to restrict
     * @param reason Human-readable reason for the restriction
     * @return Result indicating success/failure and capability info
     */
    suspend fun enableRestriction(
        apps: List<String>,
        reason: String
    ): RestrictionResult

    /**
     * Disables restriction for the given app package names.
     * @param apps Package names to unrestrict
     * @return Result indicating success/failure
     */
    suspend fun disableRestriction(
        apps: List<String>
    ): RestrictionResult

    /**
     * Checks if restriction is currently active for a given package.
     */
    suspend fun isRestrictionActive(packageName: String): Boolean

    /**
     * Checks the device's capability to support app restrictions.
     */
    suspend fun checkCapability(): RestrictionCapability

    /**
     * Returns whether the required permission/setup has been granted.
     */
    suspend fun hasRequiredPermission(): Boolean

    /**
     * Opens the system settings screen where the user can grant
     * the required permission for app restriction.
     */
    suspend fun requestPermissionSetup()
}
