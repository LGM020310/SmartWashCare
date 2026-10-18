package com.smartwashcare.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = TealLight,
    onPrimaryContainer = TealDark,
    secondaryContainer = TealLight,
    onSecondaryContainer = TealDark,
    background = AppBackground,
    onBackground = TealDark,
    surface = CardWhite,
    onSurface = TealDark,
    surfaceVariant = TealLight,
    onSurfaceVariant = TextSub,
    outline = OutlineSoft,
    error = Color(0xFFD64545)
)

@Composable
fun SmartWashCareTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}