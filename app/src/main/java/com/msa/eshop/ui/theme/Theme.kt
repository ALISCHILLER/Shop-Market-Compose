package com.msa.eshop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = EShopRed,
    onPrimary = EShopSurface,
    primaryContainer = EShopRedLight,
    onPrimaryContainer = EShopRedDark,

    secondary = EShopRedDark,
    onSecondary = EShopSurface,
    secondaryContainer = EShopRedSoft,
    onSecondaryContainer = EShopRedDarker,

    tertiary = EShopInfo,
    onTertiary = EShopSurface,
    tertiaryContainer = EShopInfoLight,
    onTertiaryContainer = EShopInfo,

    background = EShopBackground,
    onBackground = EShopTextPrimary,

    surface = EShopSurface,
    onSurface = EShopTextPrimary,

    surfaceVariant = EShopSurfaceVariant,
    onSurfaceVariant = EShopTextSecondary,

    outline = EShopBorder,
    outlineVariant = EShopSurfaceVariant,

    error = EShopError,
    onError = EShopSurface,
    errorContainer = EShopErrorLight,
    onErrorContainer = EShopRedDark
)

private val DarkColorScheme = darkColorScheme(
    primary = EShopRed,
    onPrimary = EShopSurface,
    primaryContainer = EShopRedDark,
    onPrimaryContainer = EShopSurface,

    secondary = EShopRedLight,
    onSecondary = EShopDarkBackground,
    secondaryContainer = EShopRedDark,
    onSecondaryContainer = EShopSurface,

    tertiary = EShopInfoLight,
    onTertiary = EShopDarkBackground,
    tertiaryContainer = EShopInfo,
    onTertiaryContainer = EShopSurface,

    background = EShopDarkBackground,
    onBackground = EShopDarkTextPrimary,

    surface = EShopDarkSurface,
    onSurface = EShopDarkTextPrimary,

    surfaceVariant = EShopDarkSurfaceVariant,
    onSurfaceVariant = EShopDarkTextSecondary,

    outline = EShopDarkBorder,
    outlineVariant = EShopDarkSurfaceVariant,

    error = EShopRedSoft,
    onError = EShopDarkBackground,
    errorContainer = EShopRedDark,
    onErrorContainer = EShopSurface
)

val EShopShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
)

@Composable
fun EShopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> {
            dynamicDarkColorScheme(context)
        }

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window

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
        shapes = EShopShapes,
        content = content
    )
}