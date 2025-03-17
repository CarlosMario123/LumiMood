// RecommendationsScreen.kt
package com.example.psicologic.feature_recommendations.presentation.recommendations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.psicologic.core.di.ServiceLocator
import com.example.psicologic.core.di.ViewModelFactory
import com.example.psicologic.core.presentation.components.PsicologicButton
import com.example.psicologic.feature_recommendations.domain.models.Recommendation
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla para mostrar y añadir recomendaciones
 */
@Composable
fun RecommendationsScreen(
    modifier: Modifier = Modifier
) {
    // Obtener contexto y ViewModel
    val context = LocalContext.current
    val viewModel = viewModel<RecommendationsViewModel>(
        factory = ViewModelFactory {
            ServiceLocator.provideRecommendationsViewModel(context)
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val newTitle by viewModel.newTitle.collectAsState()
    val newDescription by viewModel.newDescription.collectAsState()

    // Estado del diálogo para añadir recomendación
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título y botón para añadir
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recomendaciones",
                    style = MaterialTheme.typography.h5
                )

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir recomendación"
                    )
                }
            }

            // Lista de recomendaciones
            if (recommendations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay recomendaciones disponibles",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(recommendations) { recommendation ->
                        RecommendationItem(recommendation = recommendation)
                    }
                }
            }
        }

        // Diálogo para añadir recomendación
        if (showAddDialog) {
            AddRecommendationDialog(
                title = newTitle,
                description = newDescription,
                onTitleChange = viewModel::onNewTitleChanged,
                onDescriptionChange = viewModel::onNewDescriptionChanged,
                onDismiss = { showAddDialog = false },
                onConfirm = {
                    viewModel.addRecommendation()
                    showAddDialog = false
                }
            )
        }

        // Indicador de carga
        if (uiState is RecommendationsUiState.Loading) {
            CircularProgressIndicator()
        }

        // Mensaje de error
        if (uiState is RecommendationsUiState.Error) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.loadRecommendations() }) {
                        Text("Reintentar")
                    }
                }
            ) {
                Text((uiState as RecommendationsUiState.Error).message)
            }
        }

        // Mensaje de éxito
        LaunchedEffect(uiState) {
            if (uiState is RecommendationsUiState.Success) {
                // Aquí podrías mostrar un mensaje de éxito temporal
            }
        }
    }
}

/**
 * Elemento de recomendación en la lista
 */
@Composable
fun RecommendationItem(
    recommendation: Recommendation
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(recommendation.timestamp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Título con icono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
            }

            // Descripción
            Text(
                text = recommendation.description,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Fecha
            Text(
                text = "Creado el $formattedDate",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

/**
 * Diálogo para añadir una nueva recomendación
 */
@Composable
fun AddRecommendationDialog(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Añadir recomendación")
        },
        text = {
            Column {
                // Título
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                // Descripción
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            PsicologicButton(
                text = "Guardar",
                onClick = onConfirm,
                enabled = title.isNotBlank() && description.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}