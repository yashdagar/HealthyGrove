package com.sdgwarriors.healthygrove.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme as MaterialTheme3
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFF00e600),
    primaryVariant = Color(0xFF005300),
    secondary = Color(0xFFCCC2DC),
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF006e00),
    primaryVariant = Color(0xFF73ff5a),
    secondary = Color(0xFF625B71),
)

private val LightThemeColors = lightColorScheme(
    primary = Color(0xFF006e00),
    onPrimary = Color(0xFFffffff),
    primaryContainer = Color(0xFF73ff5a),
    onPrimaryContainer = Color(0xFF002200),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31111D),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFfbfdfd),
    onBackground = Color(0xFF191c1d),
    surface = Color(0xFFfbfdfd),
    onSurface = Color(0xFF191c1d),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    inverseOnSurface = Color(0xFFeff1f1),
    inverseSurface = Color(0xFF2d3132),
    inversePrimary = Color(0xFF00e600)
)

private val DarkThemeColors = darkColorScheme(
    primary = Color(0xFF00e600),
    onPrimary = Color(0xFF003a00),
    primaryContainer = Color(0xFF005300),
    onPrimaryContainer = Color(0xFF73ff5a),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    error = Color(0xFFF2B8B5),
    errorContainer = Color(0xFF8C1D18),
    onError = Color(0xFF601410),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF191c1d),
    onBackground = Color(0xFFe0e3e3),
    surface = Color(0xFF191c1d),
    onSurface = Color(0xFFe0e3e3),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    inverseOnSurface = Color(0xFF191c1d),
    inverseSurface = Color(0xFFe0e3e3),
    inversePrimary = Color(0xFF006e00)
)

@Composable
fun MyApplicationTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorsOld = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val colors = if (!darkTheme) {
        DarkThemeColors
    } else {
        LightThemeColors
    }

    MaterialTheme3(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )

    MaterialTheme(
        colors = colorsOld,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}