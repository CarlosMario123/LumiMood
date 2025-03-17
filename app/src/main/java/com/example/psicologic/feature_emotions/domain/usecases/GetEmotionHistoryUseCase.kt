package com.example.psicologic.feature_emotions.domain.usecases

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_emotions.domain.models.Emotion
import com.example.psicologic.feature_emotions.domain.repository.EmotionsRepository

/**
 * Caso de uso para obtener el historial de emociones
 */
class GetEmotionHistoryUseCase(private val emotionsRepository: EmotionsRepository) {

    /**
     * Ejecuta el caso de uso para obtener el historial de emociones
     *
     * @return Lista de emociones registradas
     */
    suspend operator fun invoke(): Result<List<Emotion>> {
        return emotionsRepository.getEmotionHistory()
    }
}