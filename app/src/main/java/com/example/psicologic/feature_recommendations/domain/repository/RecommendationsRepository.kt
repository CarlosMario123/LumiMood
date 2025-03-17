package com.example.psicologic.feature_recommendations.domain.repository


import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_recommendations.domain.models.Recommendation

/**
 * Interfaz del repositorio para operaciones con recomendaciones
 */
interface RecommendationsRepository {
    /**
     * Obtiene la lista de recomendaciones
     */
    suspend fun getRecommendations(): Result<List<Recommendation>>

    /**
     * Añade una nueva recomendación
     */
    suspend fun addRecommendation(title: String, description: String): Result<Recommendation>
}