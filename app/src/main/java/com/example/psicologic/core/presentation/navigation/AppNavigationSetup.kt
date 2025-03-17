package com.example.psicologic.core.presentation.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.psicologic.core.di.ServiceLocator

/**
 * Main entry point for the app's navigation
 *
 * @param startDestination The starting destination route (based on login state)
 * @param navController Navigation controller to use or create a new one
 * @param deepLinkIntent Optional intent for deep linking
 */
@Composable
fun SetupAppNavigation(
    startDestination: String? = null,
    navController: NavHostController = rememberNavController(),
    deepLinkIntent: Intent? = null
) {
    val context = LocalContext.current

    // Process deep link if provided
    deepLinkIntent?.let {
        handleDeepLink(it, navController)
    }

    // Determine the correct start destination
    val destination = startDestination ?: determineStartDestination(context)

    // Launch the navigation graph
    PsicologicNavGraph(
        navController = navController,
        startDestination = destination
    )
}

/**
 * Determines the start destination based on user login state
 */
private fun determineStartDestination(context: Context): String {
    val isLoggedIn = ServiceLocator.provideSharedPreferencesManager(context).isUserLoggedIn()

    return if (isLoggedIn) {
        NavigationRoutes.EmotionsTab.route
    } else {
        NavigationRoutes.Login.route
    }
}

/**
 * Handles deep links from notifications or external sources
 */
private fun handleDeepLink(intent: Intent, navController: NavHostController) {
    // Example of handling deep links from notifications
    when (intent.getStringExtra("navigation_type")) {
        "question" -> {
            val questionId = intent.getIntExtra("question_id", -1)
            if (questionId != -1) {
                navController.navigate(NavigationRoutes.Answer.createRoute(questionId))
            }
        }
        "emotion" -> {
            navController.navigate(NavigationRoutes.EmotionsTab.route)
        }
        "recommendation" -> {
            navController.navigate(NavigationRoutes.RecommendationsTab.route)
        }
    }
}