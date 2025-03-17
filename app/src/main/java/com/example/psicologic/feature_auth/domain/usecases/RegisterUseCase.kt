package com.example.psicologic.feature_auth.domain.usecases

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_auth.domain.repository.AuthRepository

/**
 * Caso de uso para registrar un nuevo usuario
 */
class RegisterUseCase(private val authRepository: AuthRepository) {
    /**
     * Ejecuta el caso de uso de registro
     */
    suspend operator fun invoke(name: String, email: String, password: String, fcmToken: String): Result<Boolean> {
        // Validar entradas
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.Error(Exception("Todos los campos son requeridos"))
        }

        // Validar formato de email
        if (!isEmailValid(email)) {
            return Result.Error(Exception("El formato del email no es válido"))
        }

        // Validar contraseña
        if (password.length < 6) {
            return Result.Error(Exception("La contraseña debe tener al menos 6 caracteres"))
        }

        return authRepository.register(name, email, password, fcmToken)
    }

    /**
     * Valida el formato del email
     */
    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailRegex.toRegex())
    }
}