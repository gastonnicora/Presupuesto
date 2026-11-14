package com.gastonnicora.presupuesto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores fijos de marca (no se usan colores dinámicos del sistema para que la app
// se vea siempre igual en todos los celulares).
private val LightColors = lightColorScheme(
    primary = FieldGreen,
    onPrimary = Color.White,
    primaryContainer = FieldGreenContainer,
    onPrimaryContainer = FieldGreenDeep,
    secondary = Olive,
    onSecondary = Color.White,
    secondaryContainer = OliveContainer,
    onSecondaryContainer = Color(0xFF1A2109),
    tertiary = Ochre,
    onTertiary = Color.White,
    tertiaryContainer = OchreContainer,
    onTertiaryContainer = OchreDeep,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceContainerLow = PaperContainer,
    surfaceContainer = PaperContainerHigh,
    onSurfaceVariant = InkVariant
)

private val DarkColors = darkColorScheme(
    primary = FieldGreenLight,
    onPrimary = FieldGreenDeep,
    primaryContainer = FieldGreenContainerDark,
    onPrimaryContainer = FieldGreenContainer,
    secondary = OliveLight,
    onSecondary = Color(0xFF2B331E),
    secondaryContainer = OliveContainerDark,
    onSecondaryContainer = OliveContainer,
    tertiary = OchreLight,
    onTertiary = OchreDeep,
    tertiaryContainer = OchreContainerDark,
    onTertiaryContainer = OchreContainer,
    background = NightSurface,
    onBackground = NightInk,
    surface = NightSurface,
    onSurface = NightInk,
    surfaceContainerLow = NightContainerLow,
    surfaceContainer = NightContainer,
    onSurfaceVariant = NightInkVariant
)

@Composable
fun PresupuestoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
