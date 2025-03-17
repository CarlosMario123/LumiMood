// EmotionsScreen.kt
package com.example.psicologic.feature_emotions.presentation.emotions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items  // Importación explícita de la extensión items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.psicologic.core.di.ServiceLocator
import com.example.psicologic.core.di.ViewModelFactory
import com.example.psicologic.core.presentation.components.PsicologicButton
import com.example.psicologic.core.presentation.navigation.AppIcons
import com.example.psicologic.feature_emotions.domain.models.Emotion  // CORREGIDO: "models" en lugar de "model"
import java.text.SimpleDateFormat
import java.util.*

/**
 * Datos para cada emoción disponible
 */
data class EmotionData(
    val name: String,
    val displayName: String,
    val color: String,
    val icon: ImageVector
)

/**
 * Pantalla para registrar emociones
 */
@Composable
fun EmotionsScreen(
    modifier: Modifier = Modifier
) {
    // Obtener el contexto y ViewModel
    val context = LocalContext.current
    val viewModel = viewModel<EmotionsViewModel>(
        factory = ViewModelFactory {
            ServiceLocator.provideEmotionsViewModel(context)
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val emotionHistory by viewModel.emotionHistory.collectAsState()

    // Lista de emociones disponibles
    val emotions = listOf(
        EmotionData(
            name = "feliz",
            displayName = "Feliz",
            color = "#FFD700",
            icon = AppIcons.Emotions.Happy
        ),
        EmotionData(
            name = "triste",
            displayName = "Triste",
            color = "#0000FF",
            icon = AppIcons.Emotions.Sad
        ),
        EmotionData(
            name = "estresado",
            displayName = "Estresado",
            color = "#FF0000",
            icon = AppIcons.Emotions.Stressed
        ),
        EmotionData(
            name = "enojado",
            displayName = "Enojado",
            color = "#8B0000",
            icon = AppIcons.Emotions.Angry
        ),
        EmotionData(
            name = "motivado",
            displayName = "Motivado",
            color = "#00FF00",
            icon = AppIcons.Emotions.Motivated
        ),
        EmotionData(
            name = "cansado",
            displayName = "Cansado",
            color = "#A9A9A9",
            icon = AppIcons.Emotions.Tired
        ),
        EmotionData(
            name = "ansioso",
            displayName = "Ansioso",
            color = "#FFA500",
            icon = AppIcons.Emotions.Anxious
        ),
        EmotionData(
            name = "relajado",
            displayName = "Relajado",
            color = "#87CEEB",
            icon = AppIcons.Emotions.Relaxed
        )
    )

    // Efecto para mostrar un mensaje cuando una emoción es registrada
    LaunchedEffect(uiState) {
        if (uiState is EmotionsUiState.Success) {
            // Aquí podrías mostrar un Snackbar o realizar alguna otra acción
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "¿Cómo te sientes ahora?",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "Selecciona la emoción que mejor refleje cómo te sientes en este momento.",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Grid de emociones
            EmotionsGrid(
                emotions = emotions,
                onEmotionSelected = { name, color ->
                    viewModel.registerEmotion(name, color)
                }
            )

            // Historial de emociones
            Text(
                text = "Historial de emociones",
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            if (emotionHistory.isEmpty()) {
                Text(
                    text = "Aún no has registrado emociones",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(emotionHistory) { emotion ->
                        EmotionHistoryItem(emotion = emotion)
                    }
                }
            }
        }

        // Indicador de carga
        if (uiState is EmotionsUiState.Loading) {
            CircularProgressIndicator()
        }

        // Mensaje de error
        if (uiState is EmotionsUiState.Error) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.loadEmotionHistory() }) {
                        Text("Reintentar")
                    }
                }
            ) {
                Text((uiState as EmotionsUiState.Error).message)
            }
        }
    }
}

/**
 * Grid de botones de emociones
 */
@Composable
fun EmotionsGrid(
    emotions: List<EmotionData>,
    onEmotionSelected: (String, String) -> Unit
) {
    // 2 columnas
    val chunkedEmotions = emotions.chunked(2)

    Column {
        chunkedEmotions.forEach { rowEmotions ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowEmotions.forEach { emotion ->
                    EmotionButton(
                        emotion = emotion,
                        onClick = {
                            onEmotionSelected(emotion.name, emotion.color)
                        },
                        modifier = Modifier.weight(1f, fill = true)
                    )
                }

                // Si hay un número impar de emociones en la última fila
                if (rowEmotions.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Botón para seleccionar una emoción
 */
@Composable
fun EmotionButton(
    emotion: EmotionData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(emotion.color))
    } catch (e: Exception) {
        MaterialTheme.colors.primary
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(backgroundColor.copy(alpha = 0.15f))
                .padding(8.dp)
        ) {
            Icon(
                imageVector = emotion.icon,
                contentDescription = emotion.displayName,
                tint = backgroundColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = emotion.displayName,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Elemento del historial de emociones
 */
@Composable
fun EmotionHistoryItem(emotion: Emotion) {
    val emotionColor = try {
        Color(android.graphics.Color.parseColor(emotion.color))
    } catch (e: Exception) {
        MaterialTheme.colors.primary
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(emotion.timestamp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo de color
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(emotionColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info de la emoción
            Column {
                Text(
                    text = emotion.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    },
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}