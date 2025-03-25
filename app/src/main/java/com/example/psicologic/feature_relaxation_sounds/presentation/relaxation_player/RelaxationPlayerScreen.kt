package com.example.psicologic.feature_relaxation_sounds.presentation.relaxation_player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.psicologic.core.presentation.theme.LightBlue
import com.example.psicologic.feature_relaxation_sounds.presentation.relaxation_player.components.SoundCard
import com.example.psicologic.feature_relaxation_sounds.presentation.relaxation_player.components.SoundWaveAnimation
import com.example.psicologic.feature_relaxation_sounds.presentation.theme.RelaxationTheme

@Composable
fun RelaxationPlayerScreen(
    viewModel: RelaxationPlayerViewModel
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // Inicializar el ViewModel con el contexto
    LaunchedEffect(key1 = true) {
        viewModel.initialize(context)
    }

    // Efecto para vincular/desvincular el servicio según el ciclo de vida
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.bindService(context)
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.unbindService(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Determinar si hay un sonido reproduciéndose o pausado
    val isPlayingAny = state.isPlaying && state.selectedSoundId != null
    val isPausedAny = !state.isPlaying && state.selectedSoundId != null

    // Obtener el nombre del sonido seleccionado
    val currentSoundName = state.sounds.find { it.id == state.selectedSoundId }?.name ?: ""

    // Definir colores para el tema
    val primaryColor = MaterialTheme.colors.primary
    val backgroundColor = Color(0xFFF8F8F8)
    val cardBackgroundColor = Color.White
    val accentColor = LightBlue

    // Animación para el reproductor flotante
    val playerVisibility = state.selectedSoundId != null
    val playerAlpha by animateFloatAsState(
        targetValue = if (playerVisibility) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Sonidos Relajantes",
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                backgroundColor = primaryColor,
                elevation = 0.dp
            )
        },
        bottomBar = {
            // Reproductor persistente en la parte inferior
            AnimatedVisibility(
                visible = playerVisibility,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .alpha(playerAlpha),
                    elevation = 16.dp,
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icono animado
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPlayingAny) {
                                SoundWaveAnimation(
                                    isPlaying = true,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.VolumeUp,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Información del sonido
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = currentSoundName,
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Barra de progreso lineal
                            LinearProgressIndicator(
                                progress = 0.3f, // Esto se conectaría al progreso real
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(1.5.dp)),
                                color = Color.White,
                                backgroundColor = Color.White.copy(alpha = 0.3f)
                            )
                        }

                        // Botones de control
                        Row {
                            IconButton(
                                onClick = { viewModel.pauseResumeSound(context) }
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAny) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlayingAny) "Pausar" else "Reproducir",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = { viewModel.stopSound(context) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Detener",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            // Fondo decorativo con gradiente
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.05f),
                                accentColor.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            // Patrón de elementos decorativos (separado del Box)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.05f)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Dibujar círculos decorativos con opacidad muy baja
                for (i in 0..5) {
                    val x = canvasWidth * (0.1f + i * 0.15f)
                    val y = canvasHeight * (0.1f + (i % 3) * 0.25f)
                    val radius = 80f + (i * 20f)

                    drawCircle(
                        color = primaryColor,
                        radius = radius,
                        center = Offset(x, y)
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = primaryColor
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Mensaje de bienvenida
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        backgroundColor = cardBackgroundColor,
                        shape = RoundedCornerShape(16.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Música para tu bienestar",
                                style = MaterialTheme.typography.h5,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Escucha estos sonidos relajantes para mejorar tu estado de ánimo y concentración. La música continuará incluso si cierras la aplicación.",
                                style = MaterialTheme.typography.body1,
                                color = Color.Gray
                            )

                            // Mostrar mensaje sobre optimización de batería si es necesario
                            if (!state.batteryOptimizationIgnored) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    backgroundColor = LightBlue.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Spa,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(20.dp)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = "Para una mejor experiencia, permite que la aplicación funcione en segundo plano",
                                            style = MaterialTheme.typography.caption,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Lista de sonidos
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(bottom = if (playerVisibility) 80.dp else 16.dp)
                    ) {
                        items(state.sounds) { sound ->
                            val isSelected = state.selectedSoundId == sound.id
                            SoundCard(
                                sound = sound,
                                isSelected = isSelected,
                                isPlaying = isSelected && state.isPlaying,
                                onPlayClick = {
                                    if (isSelected && !state.isPlaying) {
                                        viewModel.pauseResumeSound(context)
                                    } else {
                                        viewModel.playSound(context, sound.id)
                                    }
                                },
                                onPauseClick = { viewModel.pauseResumeSound(context) },
                                onStopClick = { viewModel.stopSound(context) }
                            )
                        }
                    }
                }
            }
        }
    }
}