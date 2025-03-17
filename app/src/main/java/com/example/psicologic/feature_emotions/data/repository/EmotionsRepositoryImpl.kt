// EmotionsRepositoryImpl.kt
package com.example.psicologic.feature_emotions.data.repository

import com.example.psicologic.core.data.local.remote.ApiService
import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_emotions.data.models.EmotionRequest
import com.example.psicologic.feature_emotions.domain.models.Emotion
import com.example.psicologic.feature_emotions.domain.repository.EmotionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Implementación del repositorio de emociones
 */
class EmotionsRepositoryImpl(
    private val apiService: ApiService
) : EmotionsRepository {

    override suspend fun registerEmotion(emotion: String, color: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val request = EmotionRequest(emotion, color)
                val response = apiService.registerEmotion(request)

                if (response.isSuccessful) {
                    Result.Success(true)
                } else {
                    Result.Error(Exception("Error al registrar emoción: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getEmotionHistory(): Result<List<Emotion>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEmotionHistory()

                if (response.isSuccessful && response.body() != null) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

                    val emotions = response.body()!!.map { emotionResponse ->
                        // Creamos un nuevo objeto Emotion con datos predeterminados o mapeados
                        // No asumimos que emotionResponse tiene un campo createdAt
                        Emotion(
                            id = emotionResponse.id ?: UUID.randomUUID().toString(),
                            name = emotionResponse.emotion ?: "",
                            color = emotionResponse.color ?: "#FFFFFF",
                            timestamp = Date() // Usamos la fecha actual como predeterminado
                        )
                    }
                    Result.Success(emotions)
                } else {
                    Result.Error(Exception("Error al obtener historial de emociones: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}