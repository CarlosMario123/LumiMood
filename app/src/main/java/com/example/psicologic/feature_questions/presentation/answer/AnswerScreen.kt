// AnswerScreen.kt
package com.example.psicologic.feature_questions.presentation.answer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.psicologic.core.di.ServiceLocator
import com.example.psicologic.core.di.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla para ver y responder a una pregunta específica
 */
@Composable
fun AnswerScreen(
    questionId: Int,
    onBackClick: () -> Unit
) {
    // Obtener contexto y ViewModel
    val context = LocalContext.current
    val viewModel = viewModel<AnswerViewModel>(
        factory = ViewModelFactory {
            ServiceLocator.provideAnswerViewModel(questionId, context)
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val question by viewModel.question.collectAsState()
    val answerText by viewModel.answer.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Pregunta y respuesta") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Pregunta principal
                question?.let { q ->
                    QuestionCard(question = q.question, date = q.timestamp)

                    // Si ya tiene respuesta, mostrarla
                    if (q.response != null) {
                        Text(
                            text = "Respuesta",
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = 2.dp,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = q.response,
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    } else {
                        // Cuando no hay respuesta todavía
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aún no hay respuesta",
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Campo para responder (solo si no hay respuesta aún)
                    if (q.response == null) {
                        OutlinedTextField(
                            value = answerText,
                            onValueChange = viewModel::onAnswerChanged,
                            label = { Text("Escribe tu respuesta...") },
                            trailingIcon = {
                                IconButton(
                                    onClick = { viewModel.submitAnswer() },
                                    enabled = answerText.isNotBlank()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Enviar respuesta"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    }
                }
            }

            // Indicador de carga
            if (uiState is AnswerUiState.Loading) {
                CircularProgressIndicator()
            }

            // Mensaje de error
            if (uiState is AnswerUiState.Error) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.loadQuestion() }) {
                            Text("Reintentar")
                        }
                    }
                ) {
                    Text((uiState as AnswerUiState.Error).message)
                }
            }
        }
    }
}

/**
 * Card que muestra la pregunta principal
 */
@Composable
fun QuestionCard(question: String, date: Date) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.h6
            )

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}