package com.zxx.linkgpt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
//    primary = Purple200,
//    primaryVariant = Purple700,
//    secondary = Teal200,
    primary = PrimaryLightBlue,
    primaryVariant = NearlyBlack,
    secondary = Color.Gray,
    secondaryVariant = MessageBlack,
    onSecondary = Color.White,
    surface = Color.Black,
    background = Color.Black,
)

private val LightColorPalette = lightColors(
//    primary = Purple500,
//    primaryVariant = Purple700,
//    secondary = Teal200,
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
    primary = PrimaryBlue,
    primaryVariant = TopBarBlue,
    secondary = Color.White,
    secondaryVariant = Color.White,
    onSecondary = Color.Black,
    surface = SurfaceWhite
)

@Composable
fun LinkGPTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}