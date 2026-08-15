package com.focusflow.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import com.focusflow.app.domain.model.ThemeMode

val LocalSpacing = staticCompositionLocalOf { Spacing }

private val LightColorScheme = lightColorScheme(
    primary = Lavender50,
    onPrimary = Color.White,
    primaryContainer = Lavender90,
    onPrimaryContainer = Lavender10,
    secondary = Lilac50,
    onSecondary = Color.White,
    secondaryContainer = Lilac90,
    onSecondaryContainer = Lilac10,
    tertiary = Amber50,
    onTertiary = Color.White,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber10,
    error = Red40,
    errorContainer = Red90,
    onError = Color.White,
    onErrorContainer = Red10,
    background = Neutral98, // Very light lavender (#F8F5FF)
    onBackground = Neutral10, // Near-black primary text
    surface = Color.White, // White with subtle tint could also be applied, we use white here for cards
    onSurface = Neutral10,
    surfaceVariant = Neutral95,
    onSurfaceVariant = Neutral40, // Muted gray secondary text
    outline = NeutralVariant50,
    inverseOnSurface = Neutral95,
    inverseSurface = Neutral20,
    inversePrimary = Lavender80,
    surfaceTint = Lavender50,
)

private val DarkColorScheme = darkColorScheme(
    primary = Lavender80,
    onPrimary = Lavender20,
    primaryContainer = Lavender30,
    onPrimaryContainer = Lavender90,
    secondary = Lilac80,
    onSecondary = Lilac20,
    secondaryContainer = Lilac30,
    onSecondaryContainer = Lilac90,
    tertiary = Amber80,
    onTertiary = Amber20,
    tertiaryContainer = Amber30,
    onTertiaryContainer = Amber90,
    error = Red80,
    errorContainer = Red30,
    onError = Red20,
    onErrorContainer = Red90,
    background = Neutral10, // Deep purple/navy tone mapped to Neutral10
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    outline = NeutralVariant60,
    inverseOnSurface = Neutral10,
    inverseSurface = Neutral90,
    inversePrimary = Lavender40,
    surfaceTint = Lavender80,
)

@Composable
fun FocusFlowTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalSpacing provides Spacing) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FocusFlowTypography,
            shapes = FocusFlowShapes,
            content = content
        )
    }
}
