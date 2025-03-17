package com.example.psicologic.feature_auth.domain.usecases

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_auth.domain.repository.AuthRepository

/**
 * Caso de uso para cerrar sesión
 */
class LogoutUseCase(private val authRepository: AuthRepository) {
    /**
     * Ejecuta el caso de uso de cierre de sesión
     */
    suspend operator fun invoke(): Result<Boolean> {
        return authRepository.logout()
    }
}