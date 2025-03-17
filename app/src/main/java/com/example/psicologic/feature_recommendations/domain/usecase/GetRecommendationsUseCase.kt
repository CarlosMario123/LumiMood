package com.example.psicologic.feature_recommendations.domain.usecase

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_recommendations.domain.models.Recommendation
import com.example.psicologic.feature_recommendations.domain.repository.RecommendationsRepository

/**
 * Caso de uso para obtener la lista de recomendaciones
 */
class GetRecommendationsUseCase(private val recommendationsRepository: RecommendationsRepository) {

    /**
     * Ejecuta el caso de uso para obtener todas las recomendaciones
     */
    suspend operator fun invoke(): Result<List<Recommendation>> {
        return recommendationsRepository.getRecommendations()
    }
}
