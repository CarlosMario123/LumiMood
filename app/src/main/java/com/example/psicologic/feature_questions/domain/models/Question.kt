// Question.kt
package com.example.psicologic.feature_questions.domain.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class AnswerRequest(
    @SerializedName("messageId")
    val messageId: Int,
    val response: String
)

data class Answer(
    val id: Int,
    val response: String,
    val timestamp: Date
)


data class Question(
    val id: Int,
    val question: String,
    val response: String?, // Puede ser null si no está respondido
    val timestamp: Date,
    val hasAnswer: Boolean // Calculado basado en si response es null o no
)