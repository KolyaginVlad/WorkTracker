package ru.kolyagin.worktracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val darkColorPalette = darkColors(
    primary = Blue,
    onPrimary = White,
    primaryVariant = BlueDark,
    secondary = Green,
    onSecondary = Black,
    background = Grey
)

private val lightColorPalette = lightColors(
    primary = Blue,
    onPrimary = White,
    primaryVariant = BlueDark,
    secondary = Green,
    onSecondary = Black,
    background = Grey
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,

    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun WorkTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorPalette
    } else {
        lightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}