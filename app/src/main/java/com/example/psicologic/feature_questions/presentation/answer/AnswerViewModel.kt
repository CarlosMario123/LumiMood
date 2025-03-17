// AnswerViewModel.kt
package com.example.psicologic.feature_questions.presentation.answer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_questions.domain.models.Question
import com.example.psicologic.feature_questions.domain.usecases.AnswerQuestionUseCase
import com.example.psicologic.feature_questions.domain.usecases.GetQuestionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de responder a una pregunta
 */
class AnswerViewModel(
    private val questionId: Int,
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val answerQuestionUseCase: AnswerQuestionUseCase
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<AnswerUiState>(AnswerUiState.Initial)
    val uiState: StateFlow<AnswerUiState> = _uiState.asStateFlow()

    // Pregunta actual
    private val _question = MutableStateFlow<Question?>(null)
    val question: StateFlow<Question?> = _question.asStateFlow()

    // Respuesta que el usuario está escribiendo
    private val _answer = MutableStateFlow("")
    val answer: StateFlow<String> = _answer.asStateFlow()

    // Inicialización del ViewModel
    init {
        loadQuestion()
    }

    /**
     * Carga la pregunta por su ID
     */
    fun loadQuestion() {
        viewModelScope.launch {
            _uiState.value = AnswerUiState.Loading

            when (val result = getQuestionsUseCase.getQuestionById(questionId)) {
                is Result.Success -> {
                    _question.value = result.data
                    _uiState.value = AnswerUiState.Content
                }
                is Result.Error -> {
                    _uiState.value = AnswerUiState.Error(
                        result.exception.message ?: "Error al cargar la pregunta"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = AnswerUiState.Loading
                }
            }
        }
    }

    /**
     * Actualiza el valor de la respuesta
     */
    fun onAnswerChanged(answer: String) {
        _answer.value = answer
    }

    /**
     * Envía la respuesta a la pregunta
     */
    fun submitAnswer() {
        val answerText = _answer.value.trim()

        if (answerText.isBlank()) {
            _uiState.value = AnswerUiState.Error("La respuesta no puede estar vacía")
            return
        }

        viewModelScope.launch {
            _uiState.value = AnswerUiState.Loading

            when (val result = answerQuestionUseCase(questionId, answerText)) {
                is Result.Success -> {
                    // Limpiar la respuesta
                    _answer.value = ""

                    // Recargar la pregunta para mostrar la nueva respuesta
                    loadQuestion()

                    _uiState.value = AnswerUiState.Success
                }
                is Result.Error -> {
                    _uiState.value = AnswerUiState.Error(
                        result.exception.message ?: "Error al enviar la respuesta"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = AnswerUiState.Loading
                }
            }
        }
    }
}

/**
 * Estados de la UI para la pantalla de respuesta
 */
sealed class AnswerUiState {
    object Initial : AnswerUiState()
    object Loading : AnswerUiState()
    object Content : AnswerUiState()
    object Success : AnswerUiState()
    data class Error(val message: String) : AnswerUiState()
}