package com.example.psicologic.feature_relaxation_sounds.presentation.relaxation_player.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun SoundWaveAnimation(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary
) {
    // El número de líneas en la animación de onda
    val barCount = 5

    // Animación para cada barra
    val infiniteTransition = rememberInfiniteTransition()

    // Lista para almacenar las animaciones de altura para cada barra
    val barHeights = List(barCount) { index ->
        val delay = index * 100 // Retraso para cada barra
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0.2f at 0 with LinearEasing
                    0.9f at 300 with LinearEasing
                    0.5f at 600 with LinearEasing
                    0.8f at 900 with LinearEasing
                    0.2f at 1200 with LinearEasing
                },
                initialStartOffset = StartOffset(delay)
            )
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.height(40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            barHeights.forEachIndexed { index, heightPct ->
                val barHeight = if (isPlaying) {
                    // Cuando está reproduciendo, usar la altura animada
                    heightPct.value
                } else {
                    // Cuando está en pausa, mostrar barras con alturas estáticas
                    when (index) {
                        0, 4 -> 0.3f
                        1, 3 -> 0.5f
                        2 -> 0.7f
                        else -> 0.4f
                    }
                }

                Canvas(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .width(4.dp)
                        .fillMaxHeight()
                ) {
                    // Calcular la altura basada en el porcentaje
                    val height = size.height * barHeight

                    // Dibujar la línea
                    drawLine(
                        color = color.copy(alpha = if (isPlaying) 1f else 0.5f),
                        start = Offset(size.width / 2, size.height / 2 - height / 2),
                        end = Offset(size.width / 2, size.height / 2 + height / 2),
                        strokeWidth = size.width,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}