package com.example.psicologic.core.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.psicologic.feature_auth.presentation.login.LoginScreen
import com.example.psicologic.feature_auth.presentation.register.RegisterScreen
import com.example.psicologic.feature_emotions.presentation.emotions.EmotionsScreen
import com.example.psicologic.feature_questions.presentation.answer.AnswerScreen
import com.example.psicologic.feature_questions.presentation.questions.QuestionsScreen
import com.example.psicologic.feature_recommendations.presentation.recommendations.RecommendationsScreen

/**
 * Main navigation graph for the application
 *
 * @param navController The navigation controller
 * @param startDestination The starting destination route
 */
@Composable
fun PsicologicNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationRoutes.Login.route
) {
    // Get current route for bottom navigation highlighting
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scaffoldState = rememberScaffoldState()

    // Routes that show bottom navigation
    val bottomNavRoutes = listOf(
        NavigationRoutes.EmotionsTab.route,
        NavigationRoutes.QuestionsTab.route,
        NavigationRoutes.AnswersTab.route,
        NavigationRoutes.RecommendationsTab.route
    )

    // Bottom navigation items
    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Emociones",
            icon = AppIcons.BottomNav.Emotions,
            route = NavigationRoutes.EmotionsTab
        ),
        BottomNavItem(
            title = "Preguntas",
            icon = AppIcons.BottomNav.Questions,
            route = NavigationRoutes.QuestionsTab
        ),
        BottomNavItem(
            title = "Respuestas",
            icon = AppIcons.BottomNav.Answers,
            route = NavigationRoutes.AnswersTab
        ),
        BottomNavItem(
            title = "Consejos",
            icon = AppIcons.BottomNav.Recommendations,
            route = NavigationRoutes.RecommendationsTab
        )
    )

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            if (bottomNavRoutes.contains(currentRoute)) {
                PsicologicBottomNavigation(
                    items = bottomNavItems,
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth routes
            composable(NavigationRoutes.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(NavigationRoutes.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(NavigationRoutes.EmotionsTab.route) {
                            popUpTo(NavigationRoutes.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavigationRoutes.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.navigate(NavigationRoutes.Login.route) {
                            popUpTo(NavigationRoutes.Register.route) { inclusive = true }
                        }
                    },
                    onRegisterSuccess = {
                        navController.navigate(NavigationRoutes.Login.route) {
                            popUpTo(NavigationRoutes.Register.route) { inclusive = true }
                        }
                    }
                )
            }

            // Main tabs
            composable(NavigationRoutes.EmotionsTab.route) {
                EmotionsScreen()
            }

            composable(NavigationRoutes.QuestionsTab.route) {
                QuestionsScreen(
                    onQuestionSelected = { questionId ->
                        navController.navigate(NavigationRoutes.Answer.createRoute(questionId))
                    }
                )
            }

            composable(NavigationRoutes.AnswersTab.route) {
                QuestionsScreen(
                    showOnlyAnswered = true,
                    onQuestionSelected = { questionId ->
                        navController.navigate(NavigationRoutes.Answer.createRoute(questionId))
                    }
                )
            }

            composable(NavigationRoutes.RecommendationsTab.route) {
                RecommendationsScreen()
            }

            // Individual screens
            composable(
                route = NavigationRoutes.Answer.route,
                arguments = listOf(navArgument("questionId") { type = NavType.IntType })
            ) { backStackEntry ->
                val questionId = backStackEntry.arguments?.getInt("questionId") ?: -1
                AnswerScreen(
                    questionId = questionId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}