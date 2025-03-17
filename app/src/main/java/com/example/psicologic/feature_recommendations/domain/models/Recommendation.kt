package com.example.psicologic.feature_recommendations.domain.models

import java.util.Date

/**
 * Modelo de dominio para una recomendación
 */
data class Recommendation(
    val id: Int,
    val title: String,
    val description: String,
    val timestamp: Date
)