// QuestionsViewModel.kt
package com.example.psicologic.feature_questions.presentation.questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_questions.domain.models.Question
import com.example.psicologic.feature_questions.domain.usecases.AddQuestionUseCase
import com.example.psicologic.feature_questions.domain.usecases.GetQuestionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de preguntas
 */
class QuestionsViewModel(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val addQuestionUseCase: AddQuestionUseCase
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<QuestionsUiState>(QuestionsUiState.Initial)
    val uiState: StateFlow<QuestionsUiState> = _uiState.asStateFlow()

    // Lista de preguntas
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    // Nueva pregunta que el usuario está escribiendo
    private val _newQuestion = MutableStateFlow("")
    val newQuestion: StateFlow<String> = _newQuestion.asStateFlow()

    // Inicialización del ViewModel
    init {
        loadQuestions()
    }

    /**
     * Carga la lista de preguntas
     */
    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.value = QuestionsUiState.Loading

            when (val result = getQuestionsUseCase()) {
                is Result.Success -> {
                    _questions.value = result.data
                    _uiState.value = QuestionsUiState.Content
                }
                is Result.Error -> {
                    _uiState.value = QuestionsUiState.Error(
                        result.exception.message ?: "Error al cargar preguntas"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = QuestionsUiState.Loading
                }
            }
        }
    }

    /**
     * Actualiza el valor de la nueva pregunta
     */
    fun onNewQuestionChanged(question: String) {
        _newQuestion.value = question
    }

    /**
     * Añade una nueva pregunta
     */
    fun addQuestion() {
        val questionText = _newQuestion.value.trim()

        if (questionText.isBlank()) {
            _uiState.value = QuestionsUiState.Error("La pregunta no puede estar vacía")
            return
        }

        viewModelScope.launch {
            _uiState.value = QuestionsUiState.Loading

            when (val result = addQuestionUseCase(questionText)) {
                is Result.Success -> {
                    // Limpiar el campo de nueva pregunta
                    _newQuestion.value = ""

                    // Recargar la lista de preguntas
                    loadQuestions()

                    _uiState.value = QuestionsUiState.Success
                }
                is Result.Error -> {
                    _uiState.value = QuestionsUiState.Error(
                        result.exception.message ?: "Error al añadir pregunta"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = QuestionsUiState.Loading
                }
            }
        }
    }

    /**
     * Filtra preguntas según si tienen respuestas o no
     */
    fun getFilteredQuestions(showOnlyAnswered: Boolean): List<Question> {
        return if (showOnlyAnswered) {
            questions.value.filter { it.hasAnswer }
        } else {
            questions.value
        }
    }
}

/**
 * Estados de la UI para la pantalla de preguntas
 */
sealed class QuestionsUiState {
    object Initial : QuestionsUiState()
    object Loading : QuestionsUiState()
    object Content : QuestionsUiState()
    object Success : QuestionsUiState()
    data class Error(val message: String) : QuestionsUiState()
}