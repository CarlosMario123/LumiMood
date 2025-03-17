package com.example.psicologic.core.presentation.navigation

/**
 * Contains all navigation routes for the app
 */
sealed class NavigationRoutes(val route: String) {
    // Auth routes
    object Login : NavigationRoutes("login")
    object Register : NavigationRoutes("register")

    // Main app routes
    object Emotions : NavigationRoutes("emotions")
    object Questions : NavigationRoutes("questions")
    object Answer : NavigationRoutes("answer/{questionId}") {
        fun createRoute(questionId: Int) = "answer/$questionId"
    }
    object Recommendations : NavigationRoutes("recommendations")

    // Bottom navigation tabs
    object EmotionsTab : NavigationRoutes("emotions_tab")
    object QuestionsTab : NavigationRoutes("questions_tab")
    object AnswersTab : NavigationRoutes("answers_tab")
    object RecommendationsTab : NavigationRoutes("recommendations_tab")
}