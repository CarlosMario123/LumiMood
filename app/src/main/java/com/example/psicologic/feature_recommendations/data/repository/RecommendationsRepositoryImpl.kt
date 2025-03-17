// RecommendationsRepositoryImpl.kt
package com.example.psicologic.feature_recommendations.data.repository


import com.example.psicologic.core.data.local.remote.ApiService
import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_recommendations.data.models.RecommendationRequest
import com.example.psicologic.feature_recommendations.domain.models.Recommendation
import com.example.psicologic.feature_recommendations.domain.repository.RecommendationsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Implementación del repositorio de recomendaciones
 */
class RecommendationsRepositoryImpl(
    private val apiService: ApiService
) : RecommendationsRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

    override suspend fun getRecommendations(): Result<List<Recommendation>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecommendations()

                if (response.isSuccessful && response.body() != null) {
                    val recommendations = response.body()!!.map { recommendationResponse ->
                        // Convertir fecha de string a Date
                        val timestamp = try {
                            dateFormat.parse(recommendationResponse.createdAt) ?: Date()
                        } catch (e: Exception) {
                            Date()
                        }

                        Recommendation(
                            id = recommendationResponse.id,
                            title = recommendationResponse.title,
                            description = recommendationResponse.description,
                            timestamp = timestamp
                        )
                    }
                    Result.Success(recommendations)
                } else {
                    Result.Error(Exception("Error al obtener recomendaciones: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun addRecommendation(title: String, description: String): Result<Recommendation> =
        withContext(Dispatchers.IO) {
            try {
                val request = RecommendationRequest(title, description)
                val response = apiService.addRecommendation(request)

                if (response.isSuccessful && response.body() != null) {
                    val recommendationResponse = response.body()!!

                    // Convertir fecha de string a Date
                    val timestamp = try {
                        dateFormat.parse(recommendationResponse.createdAt) ?: Date()
                    } catch (e: Exception) {
                        Date()
                    }

                    val recommendation = Recommendation(
                        id = recommendationResponse.id,
                        title = recommendationResponse.title,
                        description = recommendationResponse.description,
                        timestamp = timestamp
                    )

                    Result.Success(recommendation)
                } else {
                    Result.Error(Exception("Error al añadir recomendación: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}