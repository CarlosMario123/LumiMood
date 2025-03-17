// QuestionsScreen.kt
package com.example.psicologic.feature_questions.presentation.questions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.psicologic.core.di.ServiceLocator
import com.example.psicologic.core.di.ViewModelFactory
import com.example.psicologic.feature_questions.domain.models.Question
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla para mostrar y añadir preguntas
 */
@Composable
fun QuestionsScreen(
    modifier: Modifier = Modifier,
    showOnlyAnswered: Boolean = false,
    onQuestionSelected: (Int) -> Unit = {}
) {
    // Obtener contexto y ViewModel
    val context = LocalContext.current
    val viewModel = viewModel<QuestionsViewModel>(
        factory = ViewModelFactory {
            ServiceLocator.provideQuestionsViewModel(context)
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val newQuestion by viewModel.newQuestion.collectAsState()
    val questions by viewModel.questions.collectAsState()

    // Filtrar preguntas según se requiera
    val filteredQuestions = viewModel.getFilteredQuestions(showOnlyAnswered)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = if (showOnlyAnswered) "Mis preguntas respondidas" else "Registra tus preguntas",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Input para nueva pregunta (solo si no estamos en modo 'answered')
            if (!showOnlyAnswered) {
                OutlinedTextField(
                    value = newQuestion,
                    onValueChange = viewModel::onNewQuestionChanged,
                    label = { Text("Escribe tu pregunta aquí...") },
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.addQuestion() },
                            enabled = newQuestion.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar pregunta"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            // Lista de preguntas
            if (filteredQuestions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showOnlyAnswered) "No tienes preguntas respondidas" else "No tienes preguntas registradas",
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
                    items(filteredQuestions) { question ->
                        QuestionItem(
                            question = question,
                            onClick = { onQuestionSelected(question.id) }
                        )
                    }
                }
            }
        }

        // Indicador de carga
        if (uiState is QuestionsUiState.Loading) {
            CircularProgressIndicator()
        }

        // Mensaje de error
        if (uiState is QuestionsUiState.Error) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.loadQuestions() }) {
                        Text("Reintentar")
                    }
                }
            ) {
                Text((uiState as QuestionsUiState.Error).message)
            }
        }
    }
}

/**
 * Elemento de pregunta en la lista
 */
@Composable
fun QuestionItem(
    question: Question,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(question.timestamp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Pregunta
            Text(
                text = question.question,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fecha
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )

                // Indicador de si tiene respuesta
                if (question.hasAnswer) {
                    Badge(
                        modifier = Modifier.padding(start = 8.dp),
                        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Respondida",
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente Badge personalizado
 */
@Composable
fun Badge(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primary,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(50),
        elevation = 0.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}