// Path: app/src/main/java/com/castrodev/workouttracker/core/theme/Theme.kt
package com.castrodev.workouttracker.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary   = OrangeAccent,
    secondary = Orange80,
    tertiary  = Orange40,
    background = Gray10,
    surface    = Gray20
)

private val LightColorScheme = lightColorScheme(
    primary   = OrangeAccent,
    onPrimary = Color.White,
    secondary = Orange80,
    tertiary  = Orange40,
    onSurface = Color.Black,
    onBackground = Color.Black
)

@Composable
fun WorkoutTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}