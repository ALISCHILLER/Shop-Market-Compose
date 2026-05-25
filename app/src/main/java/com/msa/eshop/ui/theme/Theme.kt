package com.msa.eshop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = EShopRed,
    onPrimary = EShopSurface,
    primaryContainer = EShopRedLight,
    onPrimaryContainer = EShopRedDark,
    secondary = EShopRedDark,
    onSecondary = EShopSurface,
    background = EShopBackground,
    onBackground = EShopTextPrimary,
    surface = EShopSurface,
    onSurface = EShopTextPrimary,
    surfaceVariant = EShopSurfaceVariant,
    onSurfaceVariant = EShopTextSecondary,
    outline = EShopBorder,
    error = EShopRed,
    onError = EShopSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = EShopRed,
    onPrimary = EShopSurface,
    primaryContainer = EShopRedDark,
    onPrimaryContainer = EShopSurface,
    secondary = EShopRedLight,
    background = ColorDark.Background,
    onBackground = ColorDark.Text,
    surface = ColorDark.Surface,
    onSurface = ColorDark.Text,
    surfaceVariant = ColorDark.SurfaceVariant,
    onSurfaceVariant = ColorDark.TextSecondary,
    outline = ColorDark.Border,
    error = EShopRed,
    onError = EShopSurface
)

private object ColorDark {
    val Background = androidx.compose.ui.graphics.Color(0xFF111113)
    val Surface = androidx.compose.ui.graphics.Color(0xFF1B1B1F)
    val SurfaceVariant = androidx.compose.ui.graphics.Color(0xFF242429)
    val Text = androidx.compose.ui.graphics.Color(0xFFF5F5F7)
    val TextSecondary = androidx.compose.ui.graphics.Color(0xFFB7B7C0)
    val Border = androidx.compose.ui.graphics.Color(0xFF34343A)
}

@Composable
fun EShopTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(context)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}