// RelaxationTheme.kt
package com.example.psicologic.feature_relaxation_sounds.presentation.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.psicologic.core.presentation.theme.LightBlue
import com.example.psicologic.core.presentation.theme.PrimaryPurple
import com.example.psicologic.core.presentation.theme.Shapes

private val LightColorPalette = lightColors(
    primary = PrimaryPurple,
    primaryVariant = Color(0xFF5E35B1),
    secondary = LightBlue,
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF333333),
    onSurface = Color(0xFF333333),
)

@Composable
fun RelaxationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        typography = MaterialTheme.typography,
        shapes = Shapes,
        content = content
    )
}