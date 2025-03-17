package com.example.psicologic.feature_emotions.domain.usecases

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_emotions.domain.repository.EmotionsRepository

/**
 * Caso de uso para registrar una emoción del usuario
 */
class RegisterEmotionUseCase(private val emotionsRepository: EmotionsRepository) {

    /**
     * Ejecuta el caso de uso para registrar una emoción
     *
     * @param emotion Nombre de la emoción (ej: "feliz", "triste")
     * @param color Código de color en formato hexadecimal
     * @return Resultado de la operación
     */
    suspend operator fun invoke(emotion: String, color: String): Result<Boolean> {
        // Validar inputs
        if (emotion.isBlank()) {
            return Result.Error(Exception("La emoción no puede estar vacía"))
        }

        if (color.isBlank()) {
            return Result.Error(Exception("El color no puede estar vacío"))
        }

        return emotionsRepository.registerEmotion(emotion, color)
    }
}