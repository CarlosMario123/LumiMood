package com.example.psicologic.feature_questions.data.models

import com.google.gson.annotations.SerializedName


data class QuestionResponse(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val question: String,
    val response: String?, // Puede ser null si no está respondido
    @SerializedName("created_at")
    val createdAt: String
)

data class ResponseData(
    val id: Int,
    val response: String,
    @SerializedName("question_id")
    val questionId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?
)