package com.example.psicologic.core.presentation.theme

import androidx.compose.ui.graphics.Color

object EmotionColors {
    // Map emotion names to their color values
    val emotionColorMap = mapOf(
        "feliz" to Color(0xFFFFD700),      // Yellow
        "triste" to Color(0xFF0000FF),     // Blue
        "estresado" to Color(0xFFFF0000),  // Red
        "enojado" to Color(0xFF8B0000),    // Dark Red
        "motivado" to Color(0xFF00FF00),   // Green
        "cansado" to Color(0xFFA9A9A9),    // Gray
        "ansioso" to Color(0xFFFFA500),    // Orange
        "relajado" to Color(0xFF87CEEB)    // Light Blue
    )

    // Get color for emotion, returning default if not found
    fun getColorForEmotion(emotion: String): Color {
        return emotionColorMap[emotion.lowercase()] ?: PrimaryPurple
    }

    // Get string HEX color for emotion for API requests
    fun getHexColorForEmotion(emotion: String): String {
        return when (emotion.lowercase()) {
            "feliz" -> "#FFD700"
            "triste" -> "#0000FF"
            "estresado" -> "#FF0000"
            "enojado" -> "#8B0000"
            "motivado" -> "#00FF00"
            "cansado" -> "#A9A9A9"
            "ansioso" -> "#FFA500"
            "relajado" -> "#87CEEB"
            else -> "#7E57C2" // Default to primary purple
        }
    }
}