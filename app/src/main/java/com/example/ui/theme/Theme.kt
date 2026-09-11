package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkHeaderText,
    onPrimary = Color.Black,
    primaryContainer = DarkPaleSageTint,
    onPrimaryContainer = DarkHeaderText,
    secondary = MintLeafAccent,
    onSecondary = Color.Black,
    background = DarkAppBackground,
    surface = DarkCardSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkPaleSageLight,
    outline = DarkCardBorder
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
  val appColors = if (darkTheme) DarkAppThemeColors else LightAppThemeColors

  CompositionLocalProvider(
    LocalAppColors provides appColors,
    LocalDarkTheme provides darkTheme
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
