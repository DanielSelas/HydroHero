package com.danielsela.hydrohero.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Feeds MaterialTheme from the HydroHero tokens so that stock M3 components
 * (Switch, Button defaults, Dialog scrim, ripples) match the hand-styled screens.
 *
 * Dynamic color is deliberately NOT used: Material You would derive its own
 * palette from the wallpaper on Android 12+ and override the hue the user picked
 * in Settings.
 *
 * Light/dark follows [HydroThemeRuntime.dark] — the in-app sun/moon toggle — not
 * the system setting, and the tokens themselves already swap on the same flag.
 */
@Composable
fun HydroHeroTheme(content: @Composable () -> Unit) {
    val colorScheme = if (HydroThemeRuntime.dark) {
        darkColorScheme(
            primary = HydroPrimary,
            onPrimary = Color.White,
            primaryContainer = HydroPrimarySoft,
            onPrimaryContainer = HydroInk,
            background = HydroSurface2,
            onBackground = HydroInk,
            surface = HydroSurface,
            onSurface = HydroInk,
            surfaceVariant = HydroSurface3,
            onSurfaceVariant = HydroInk2,
            outline = HydroLine,
        )
    } else {
        lightColorScheme(
            primary = HydroPrimary,
            onPrimary = Color.White,
            primaryContainer = HydroPrimarySoft,
            onPrimaryContainer = HydroInk,
            background = HydroSurface2,
            onBackground = HydroInk,
            surface = HydroSurface,
            onSurface = HydroInk,
            surfaceVariant = HydroSurface3,
            onSurfaceVariant = HydroInk2,
            outline = HydroLine,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
