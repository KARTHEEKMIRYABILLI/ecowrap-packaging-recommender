package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = MintLeafAccent,
    onPrimary = Color.White,
    primaryContainer = ForestGreenPrimary,
    onPrimaryContainer = Color.White,
    secondary = MintGreenLight,
    background = DarkEmeraldBackground,
    surface = Color(0xFF0F3D26),
    onBackground = Color.White,
    onSurface = Color.White,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = PaleSageTint,
    onPrimaryContainer = ForestGreenPrimary,
    secondary = MintLeafAccent,
    onSecondary = Color.White,
    background = AppBackground,
    surface = CardSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = PaleSageLight,
    outline = CardBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

