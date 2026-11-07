package com.example.carlauncher.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Palette {
    val Bg = Color(0xFF0B0E13)
    val Card = Color(0xFF161B24)
    val CardPressed = Color(0xFF222A38)
    val Accent = Color(0xFF4FC3F7)      // Android Auto tile highlight
    val AccentAlt = Color(0xFFFF8A65)   // CarPlay tile highlight
    val TextPrimary = Color(0xFFF2F5FA)
    val TextDim = Color(0xFF8B96A8)
}

@Composable
fun CarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Palette.Bg,
            surface = Palette.Card,
            primary = Palette.Accent,
            onBackground = Palette.TextPrimary,
            onSurface = Palette.TextPrimary
        ),
        content = content
    )
}
