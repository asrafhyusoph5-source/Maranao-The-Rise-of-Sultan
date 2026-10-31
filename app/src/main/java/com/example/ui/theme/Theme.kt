package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MaranaoDarkColorScheme = darkColorScheme(
  primary = MaranaoGold,
  onPrimary = ToroganWoodDark,
  primaryContainer = RoyalCrimsonDark,
  onPrimaryContainer = MaranaoGoldLight,
  secondary = MindanaoGreenLight,
  onSecondary = TextPrimary,
  secondaryContainer = MindanaoGreenDark,
  onSecondaryContainer = TextPrimary,
  tertiary = LakeLanaoSurface,
  onTertiary = TextPrimary,
  background = SurfaceDark,
  onBackground = TextPrimary,
  surface = SurfaceContainerDark,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceCard,
  onSurfaceVariant = TextSecondary,
  outline = SurfaceCardBorder
)

private val MaranaoLightColorScheme = lightColorScheme(
  primary = RoyalCrimson,
  onPrimary = TextPrimary,
  primaryContainer = MaranaoGoldLight,
  onPrimaryContainer = RoyalCrimsonDark,
  secondary = MindanaoGreen,
  onSecondary = TextPrimary,
  tertiary = LakeLanaoBlue,
  onTertiary = TextPrimary,
  background = SandStone,
  onBackground = ToroganWoodDark,
  surface = Color(0xFFF7F2E7),
  onSurface = ToroganWoodDark,
  surfaceVariant = Color(0xFFEADBCE),
  onSurfaceVariant = ToroganWoodMid,
  outline = ToroganWoodLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to immersive RPG dark atmosphere
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) MaranaoDarkColorScheme else MaranaoLightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
