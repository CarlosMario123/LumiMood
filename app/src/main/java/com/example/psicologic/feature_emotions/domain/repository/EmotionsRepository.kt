package com.example.psicologic.feature_emotions.domain.repository

import com.example.psicologic.core.domain.models.Result

import com.example.psicologic.feature_emotions.domain.models.Emotion

/**
 * Interfaz del repositorio para operaciones relacionadas con emociones
 */
interface EmotionsRepository {
    /**
     * Registra una nueva emoción del usuario
     */
    suspend fun registerEmotion(emotion: String, color: String): Result<Boolean>

    /**
     * Obtiene el historial de emociones registradas
     */
    suspend fun getEmotionHistory(): Result<List<Emotion>>
}
