package com.example.psicologic.feature_auth.presentation.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.core.notification.NotificationManager
import com.example.psicologic.feature_auth.domain.usecases.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de registro
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val notificationManager: NotificationManager
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Initial)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // Campos del formulario
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    /**
     * Actualiza el nombre
     */
    fun onNameChanged(name: String) {
        _name.value = name
    }

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
     * Actualiza la confirmación de contraseña
     */
    fun onConfirmPasswordChanged(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    /**
     * Inicia el proceso de registro
     */
    fun register() {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            try {
                // Validar entradas
                val name = _name.value.trim()
                val email = _email.value.trim()
                val password = _password.value
                val confirmPassword = _confirmPassword.value

                // Validar contraseñas iguales
                if (password != confirmPassword) {
                    _uiState.value = RegisterUiState.Error("Las contraseñas no coinciden")
                    return@launch
                }

                // Obtener token FCM para notificaciones
                val fcmToken = notificationManager.getFcmToken() ?: ""
                Log.d("fcm",fcmToken)

                // Llamar al caso de uso
                when (val result = registerUseCase(name, email, password, fcmToken)) {
                    is Result.Success -> {
                        _uiState.value = RegisterUiState.Success
                    }
                    is Result.Error -> {
                        _uiState.value = RegisterUiState.Error(result.exception.message ?: "Error desconocido")
                    }
                    is Result.Loading -> {
                        _uiState.value = RegisterUiState.Loading
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RegisterUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
