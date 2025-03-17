package com.example.psicologic.feature_recommendations.domain.usecase

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_recommendations.domain.models.Recommendation
import com.example.psicologic.feature_recommendations.domain.repository.RecommendationsRepository

/**
 * Caso de uso para añadir una nueva recomendación
 */
class AddRecommendationUseCase(private val recommendationsRepository: RecommendationsRepository) {

    /**
     * Ejecuta el caso de uso para añadir una nueva recomendación
     */
    suspend operator fun invoke(title: String, description: String): Result<Recommendation> {
        // Validar inputs
        if (title.isBlank()) {
            return Result.Error(Exception("El título no puede estar vacío"))
        }

        if (description.isBlank()) {
            return Result.Error(Exception("La descripción no puede estar vacía"))
        }

        return recommendationsRepository.addRecommendation(title, description)
    }
}
