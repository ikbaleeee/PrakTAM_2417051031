package com.example.praktam_2417051031.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = IGPrimary,
    secondary = IGSecondary,
    background = IGBackground,
    surface = IGSurface,
    onPrimary = IGOnPrimary
)

@Composable
fun PrakTAM_2417051031Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}