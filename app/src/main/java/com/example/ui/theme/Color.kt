package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Eco Wrap Base Brand Tokens
val ForestGreenPrimary = Color(0xFF0A5C36)
val MintLeafAccent = Color(0xFF2EB872)
val MintGreenLight = Color(0xFF22C55E)
val MintSoftGreen = Color(0xFFDCFCE7)
val EmeraldGreen = Color(0xFF10B981)
val PaleSageTint = Color(0xFFE8F5E9)
val PaleSageLight = Color(0xFFEDF7ED)
val AppBackground = Color(0xFFF8FAF8)
val CardSurface = Color(0xFFFFFFFF)
val CardBorder = Color(0xFFE5E7EB)
val DarkEmeraldBackground = Color(0xFF062E1B)
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF5B7065)
val TextMuted = Color(0xFF9CA3AF)
val WarningOrange = Color(0xFFF59E0B)
val InfoBlue = Color(0xFF3B82F6)

// Dark Theme Specific Tokens
val DarkAppBackground = Color(0xFF0A1811)
val DarkCardSurface = Color(0xFF12281D)
val DarkCardBorder = Color(0xFF1C3D2D)
val DarkTextPrimary = Color(0xFFF3F4F6)
val DarkTextSecondary = Color(0xFFA5C4B4)
val DarkTextMuted = Color(0xFF6B7280)
val DarkPaleSageTint = Color(0xFF163828)
val DarkPaleSageLight = Color(0xFF193F2E)
val DarkHeaderText = Color(0xFF34D399)
val DarkSurfaceVariant = Color(0xFF0E2218)

data class AppThemeColors(
  val background: Color,
  val cardBackground: Color,
  val cardBorder: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val textMuted: Color,
  val paleSageTint: Color,
  val paleSageLight: Color,
  val headerText: Color,
  val primary: Color,
  val isDark: Boolean
)

val LightAppThemeColors = AppThemeColors(
  background = AppBackground,
  cardBackground = CardSurface,
  cardBorder = CardBorder,
  textPrimary = TextPrimary,
  textSecondary = TextSecondary,
  textMuted = TextMuted,
  paleSageTint = PaleSageTint,
  paleSageLight = PaleSageLight,
  headerText = ForestGreenPrimary,
  primary = ForestGreenPrimary,
  isDark = false
)

val DarkAppThemeColors = AppThemeColors(
  background = DarkAppBackground,
  cardBackground = DarkCardSurface,
  cardBorder = DarkCardBorder,
  textPrimary = DarkTextPrimary,
  textSecondary = DarkTextSecondary,
  textMuted = DarkTextMuted,
  paleSageTint = DarkPaleSageTint,
  paleSageLight = DarkPaleSageLight,
  headerText = DarkHeaderText,
  primary = DarkHeaderText,
  isDark = true
)

val LocalAppColors = staticCompositionLocalOf { LightAppThemeColors }
val LocalDarkTheme = compositionLocalOf { false }

object AppTheme {
  val colors: AppThemeColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

  val isDark: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalDarkTheme.current
}
