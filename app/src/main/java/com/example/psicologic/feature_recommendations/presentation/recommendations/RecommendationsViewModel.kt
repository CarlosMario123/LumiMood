// RecommendationsViewModel.kt
package com.example.psicologic.feature_recommendations.presentation.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_recommendations.domain.models.Recommendation
import com.example.psicologic.feature_recommendations.domain.usecase.AddRecommendationUseCase
import com.example.psicologic.feature_recommendations.domain.usecase.GetRecommendationsUseCase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de recomendaciones
 */
class RecommendationsViewModel(
    private val getRecommendationsUseCase: GetRecommendationsUseCase,
    private val addRecommendationUseCase: AddRecommendationUseCase
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<RecommendationsUiState>(RecommendationsUiState.Initial)
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    // Lista de recomendaciones
    private val _recommendations = MutableStateFlow<List<Recommendation>>(emptyList())
    val recommendations: StateFlow<List<Recommendation>> = _recommendations.asStateFlow()

    // Nueva recomendación que el usuario está creando
    private val _newTitle = MutableStateFlow("")
    val newTitle: StateFlow<String> = _newTitle.asStateFlow()

    private val _newDescription = MutableStateFlow("")
    val newDescription: StateFlow<String> = _newDescription.asStateFlow()

    // Inicialización del ViewModel
    init {
        loadRecommendations()
    }

    /**
     * Carga la lista de recomendaciones
     */
    fun loadRecommendations() {
        viewModelScope.launch {
            _uiState.value = RecommendationsUiState.Loading

            when (val result = getRecommendationsUseCase()) {
                is Result.Success -> {
                    _recommendations.value = result.data
                    _uiState.value = RecommendationsUiState.Content
                }
                is Result.Error -> {
                    _uiState.value = RecommendationsUiState.Error(
                        result.exception.message ?: "Error al cargar recomendaciones"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = RecommendationsUiState.Loading
                }
            }
        }
    }

    /**
     * Actualiza el valor del nuevo título
     */
    fun onNewTitleChanged(title: String) {
        _newTitle.value = title
    }

    /**
     * Actualiza el valor de la nueva descripción
     */
    fun onNewDescriptionChanged(description: String) {
        _newDescription.value = description
    }

    /**
     * Añade una nueva recomendación
     */
    fun addRecommendation() {
        val title = _newTitle.value.trim()
        val description = _newDescription.value.trim()

        if (title.isBlank() || description.isBlank()) {
            _uiState.value = RecommendationsUiState.Error("El título y la descripción son obligatorios")
            return
        }

        viewModelScope.launch {
            _uiState.value = RecommendationsUiState.Loading

            when (val result = addRecommendationUseCase(title, description)) {
                is Result.Success -> {
                    // Limpiar los campos
                    _newTitle.value = ""
                    _newDescription.value = ""

                    // Recargar la lista de recomendaciones
                    loadRecommendations()

                    _uiState.value = RecommendationsUiState.Success
                }
                is Result.Error -> {
                    _uiState.value = RecommendationsUiState.Error(
                        result.exception.message ?: "Error al añadir recomendación"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = RecommendationsUiState.Loading
                }
            }
        }
    }
}

/**
 * Estados de la UI para la pantalla de recomendaciones
 */
sealed class RecommendationsUiState {
    object Initial : RecommendationsUiState()
    object Loading : RecommendationsUiState()
    object Content : RecommendationsUiState()
    object Success : RecommendationsUiState()
    data class Error(val message: String) : RecommendationsUiState()
}