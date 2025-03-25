package com.example.psicologic.feature_relaxation_sounds.presentation.relaxation_player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.psicologic.core.presentation.theme.LightBlue
import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSound

@Composable
fun SoundCard(
    sound: RelaxationSound,
    isSelected: Boolean,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Seleccionar icono según tipo de sonido
    val soundIcon = when {
        sound.name.contains("lluvia", ignoreCase = true) -> Icons.Filled.Cloud
        sound.name.contains("bosque", ignoreCase = true) -> Icons.Filled.Park
        sound.name.contains("mar", ignoreCase = true) ||
                sound.name.contains("olas", ignoreCase = true) -> Icons.Filled.Water
        sound.name.contains("noche", ignoreCase = true) -> Icons.Filled.NightShelter
        sound.name.contains("fuego", ignoreCase = true) -> Icons.Filled.LocalFireDepartment
        sound.name.contains("meditación", ignoreCase = true) -> Icons.Filled.SelfImprovement
        else -> Icons.Filled.GraphicEq
    }

    // Seleccionar color según tipo de sonido
    val iconColor = when {
        sound.name.contains("lluvia", ignoreCase = true) -> Color(0xFF64B5F6) // Azul claro
        sound.name.contains("bosque", ignoreCase = true) -> Color(0xFF81C784) // Verde
        sound.name.contains("mar", ignoreCase = true) -> Color(0xFF4FC3F7)    // Azul cielo
        sound.name.contains("noche", ignoreCase = true) -> Color(0xFF9575CD)  // Púrpura
        sound.name.contains("fuego", ignoreCase = true) -> Color(0xFFFF8A65)  // Naranja
        sound.name.contains("meditación", ignoreCase = true) -> Color(0xFFBA68C8) // Violeta
        else -> MaterialTheme.colors.primary
    }

    // Animaciones
    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 16f else 4f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    val cardColor = if (isSelected) {
        MaterialTheme.colors.primary.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colors.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .shadow(elevation.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = cardColor,
        elevation = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Círculo con icono o imagen
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.2f)
                            else Color.LightGray.copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = soundIcon,
                        contentDescription = sound.name,
                        modifier = Modifier.size(30.dp),
                        tint = if (isPlaying) iconColor else iconColor.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Información del sonido
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = sound.name,
                        style = MaterialTheme.typography.h6,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                    )

                    Text(
                        text = sound.description,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Indicador de duración
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${sound.durationSeconds / 60}:${String.format("%02d", sound.durationSeconds % 60)}",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // Botón de reproducción principal
                FloatingActionButton(
                    onClick = if (isPlaying) onPauseClick else onPlayClick,
                    modifier = Modifier.size(48.dp),
                    backgroundColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.primary.copy(alpha = 0.7f)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        tint = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Panel de controles expandido (solo visible cuando está seleccionado)
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Barra de progreso simulada (si se necesita real, habría que conectarla al estado)
                    if (isPlaying) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MaterialTheme.colors.primary,
                            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Controles adicionales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Botón de reproducción/pausa
                        OutlinedButton(
                            onClick = if (isPlaying) onPauseClick else onPlayClick,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colors.primary
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isPlaying) "Pausar" else "Reproducir")
                        }

                        // Botón de detener
                        Button(
                            onClick = onStopClick,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Stop,
                                contentDescription = "Detener",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Detener")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}