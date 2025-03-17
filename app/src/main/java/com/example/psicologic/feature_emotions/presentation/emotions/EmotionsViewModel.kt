// EmotionsViewModel.kt
package com.example.psicologic.feature_emotions.presentation.emotions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psicologic.core.domain.models.Result

import com.example.psicologic.feature_emotions.domain.models.Emotion

import com.example.psicologic.feature_emotions.domain.usecases.GetEmotionHistoryUseCase
import com.example.psicologic.feature_emotions.domain.usecases.RegisterEmotionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de emociones
 */
class EmotionsViewModel(
    private val registerEmotionUseCase: RegisterEmotionUseCase,
    private val getEmotionHistoryUseCase: GetEmotionHistoryUseCase
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<EmotionsUiState>(EmotionsUiState.Initial)
    val uiState: StateFlow<EmotionsUiState> = _uiState.asStateFlow()

    // Historial de emociones
    private val _emotionHistory = MutableStateFlow<List<Emotion>>(emptyList())
    val emotionHistory: StateFlow<List<Emotion>> = _emotionHistory.asStateFlow()

    // Inicialización del ViewModel
    init {
        loadEmotionHistory()
    }

    /**
     * Carga el historial de emociones
     */
    fun loadEmotionHistory() {
        viewModelScope.launch {
            _uiState.value = EmotionsUiState.Loading

            when (val result = getEmotionHistoryUseCase()) {
                is Result.Success -> {
                    _emotionHistory.value = result.data
                    _uiState.value = EmotionsUiState.Content
                }
                is Result.Error -> {
                    _uiState.value = EmotionsUiState.Error(
                        result.exception.message ?: "Error al cargar el historial de emociones"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = EmotionsUiState.Loading
                }
            }
        }
    }

    /**
     * Registra una nueva emoción del usuario
     */
    fun registerEmotion(emotion: String, color: String) {
        viewModelScope.launch {
            _uiState.value = EmotionsUiState.Loading

            when (val result = registerEmotionUseCase(emotion, color)) {
                is Result.Success -> {
                    _uiState.value = EmotionsUiState.Success
                    // Recargar el historial
                    loadEmotionHistory()
                }
                is Result.Error -> {
                    _uiState.value = EmotionsUiState.Error(
                        result.exception.message ?: "Error al registrar emoción"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = EmotionsUiState.Loading
                }
            }
        }
    }
}

/**
 * Estados de la UI para la pantalla de emociones
 */
sealed class EmotionsUiState {
    object Initial : EmotionsUiState()
    object Loading : EmotionsUiState()
    object Content : EmotionsUiState()
    object Success : EmotionsUiState()
    data class Error(val message: String) : EmotionsUiState()
}