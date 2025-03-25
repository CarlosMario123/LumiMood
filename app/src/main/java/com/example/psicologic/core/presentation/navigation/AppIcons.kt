package com.example.psicologic.core.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.LiveHelp
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Recommend
import androidx.compose.ui.graphics.vector.ImageVector

object AppIcons {
    /**
     * Bottom navigation icons
     */
    object BottomNav {
        val Emotions = Icons.Filled.Mood
        val Questions = Icons.Filled.QuestionAnswer
        val Answers = Icons.Rounded.LiveHelp
        val Recommendations = Icons.Filled.Lightbulb
        val Relaxation = Icons.Filled.Spa  // Nuevo ícono para la sección de relajación
    }


    object Emotions {
        val Happy = Icons.Filled.SentimentSatisfied
        val Sad = Icons.Filled.SentimentDissatisfied
        val Stressed = Icons.Filled.Warning
        val Angry = Icons.Filled.SentimentVeryDissatisfied
        val Motivated = Icons.Filled.Bolt
        val Tired = Icons.Filled.Bedtime
        val Anxious = Icons.Filled.HourglassTop
        val Relaxed = Icons.Filled.Spa

        // Map of emotion names to their icons
        val emotionIconMap = mapOf(
            "feliz" to Happy,
            "triste" to Sad,
            "estresado" to Stressed,
            "enojado" to Angry,
            "motivado" to Motivated,
            "cansado" to Tired,
            "ansioso" to Anxious,
            "relajado" to Relaxed
        )

        // Get icon for emotion name
        fun getIconForEmotion(emotion: String): ImageVector {
            return emotionIconMap[emotion.lowercase()] ?: Happy
        }
    }

    /**
     * Common action icons
     */
    object Actions {
        val Back = Icons.Filled.Home
        val Settings = Icons.Outlined.Settings
        val Profile = Icons.Rounded.Person
        val Notifications = Icons.Outlined.Notifications
        val Add = Icons.Filled.Favorite
        val Recommendation = Icons.Outlined.Psychology
        val Insight = Icons.Rounded.Insights
        val SmartTip = Icons.Outlined.Lightbulb
        val Recommend = Icons.Rounded.Recommend
        val Music = Icons.Filled.MusicNote  // Icono para música/relajación
    }
}