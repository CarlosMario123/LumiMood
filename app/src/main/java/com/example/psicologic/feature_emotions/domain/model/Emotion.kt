// Emotion.kt
package com.example.psicologic.feature_emotions.domain.models

import java.util.Date

/**
 * Modelo de dominio para una emoción
 */
data class Emotion(
    val id: String,
    val name: String,
    val color: String,
    val timestamp: Date,
    val iconResId: Int? = null
)