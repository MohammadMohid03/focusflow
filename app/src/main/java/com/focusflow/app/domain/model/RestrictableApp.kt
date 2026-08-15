package com.focusflow.app.domain.model

import android.graphics.drawable.Drawable

data class RestrictableApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val isSelected: Boolean = false,
    val isSystemApp: Boolean = false
)

enum class RestrictionCapability {
    SUPPORTED,
    REQUIRES_PERMISSION,
    UNSUPPORTED,
    ERROR
}

data class RestrictionResult(
    val success: Boolean,
    val message: String? = null,
    val capability: RestrictionCapability = RestrictionCapability.SUPPORTED
)
