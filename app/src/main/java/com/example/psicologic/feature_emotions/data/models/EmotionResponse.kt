// EmotionResponse.kt
package com.example.psicologic.feature_emotions.data.models

import com.google.gson.annotations.SerializedName

data class EmotionResponse(
    val id: String,
    val emotion: String,
    val color: String,
    @SerializedName("created_at")
    val createdAt: String?,  // Añadiendo la anotación para mapear desde JSON
    @SerializedName("user_id")
    val userId: Int?
)