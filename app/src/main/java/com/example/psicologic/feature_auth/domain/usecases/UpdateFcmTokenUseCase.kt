package com.example.psicologic.feature_auth.domain.usecases

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_auth.domain.repository.AuthRepository

/**
 * Caso de uso para actualizar el token FCM
 */
class UpdateFcmTokenUseCase(private val authRepository: AuthRepository) {
    /**
     * Ejecuta el caso de uso para actualizar el token FCM
     */
    suspend operator fun invoke(token: String): Result<Boolean> {
        if (token.isBlank()) {
            return Result.Error(Exception("El token FCM no puede estar vacío"))
        }

        return authRepository.updateFcmToken(token)
    }
}