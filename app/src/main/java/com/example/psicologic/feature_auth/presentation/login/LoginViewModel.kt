package com.example.psicologic.feature_auth.presentation.login

import LoginUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.core.notification.NotificationManager
import com.example.psicologic.feature_auth.domain.models.User
import com.example.psicologic.feature_auth.domain.usecases.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de login
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val notificationManager: NotificationManager
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Campos del formulario
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    /**
     * Actualiza el email
     */
    fun onEmailChanged(email: String) {
        _email.value = email
    }

    /**
     * Actualiza la contraseña
     */
    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    /**
     * Inicia el proceso de login
     */
    fun login() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            try {
                // Validar entrada
                val email = _email.value.trim()
                val password = _password.value.trim()

                // Llamar al caso de uso
                when (val result = loginUseCase(email, password)) {
                    is Result.Success -> {
                        _uiState.value = LoginUiState.Success(result.data)
                    }
                    is Result.Error -> {
                        _uiState.value = LoginUiState.Error(result.exception.message ?: "Error desconocido")
                    }
                    is Result.Loading -> {
                        _uiState.value = LoginUiState.Loading
                    }
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}