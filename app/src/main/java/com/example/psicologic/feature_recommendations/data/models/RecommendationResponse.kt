package com.example.psicologic.feature_recommendations.data.models

import com.google.gson.annotations.SerializedName

data class RecommendationResponse(
    val id: Int,
    val title: String,
    val description: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?
)