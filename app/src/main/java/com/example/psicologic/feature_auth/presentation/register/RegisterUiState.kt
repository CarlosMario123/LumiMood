package com.example.psicologic.feature_auth.presentation.register

/**
 * Estados UI para la pantalla de registro
 */
sealed class RegisterUiState {
    object Initial : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}