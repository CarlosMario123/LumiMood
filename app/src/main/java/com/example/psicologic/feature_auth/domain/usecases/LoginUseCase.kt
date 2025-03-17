package com.example.psicologic.feature_auth.domain.usecases

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_auth.domain.models.User
import com.example.psicologic.feature_auth.domain.repository.AuthRepository

/**
 * Caso de uso para iniciar sesión
 */
class LoginUseCase(private val authRepository: AuthRepository) {
    /**
     * Ejecuta el caso de uso de inicio de sesión
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validar entradas
        if (email.isBlank() || password.isBlank()) {
            return Result.Error(Exception("Email y contraseña son requeridos"))
        }

        // Validar formato de email
        if (!isEmailValid(email)) {
            return Result.Error(Exception("El formato del email no es válido"))
        }

        return authRepository.login(email, password)
    }

    /**
     * Valida el formato del email
     */
    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailRegex.toRegex())
    }
}
