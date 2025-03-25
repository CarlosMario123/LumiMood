package com.example.psicologic.core.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.psicologic.core.di.ServiceLocator
import com.example.psicologic.core.di.ViewModelFactory
import com.example.psicologic.feature_auth.presentation.login.LoginScreen
import com.example.psicologic.feature_auth.presentation.register.RegisterScreen
import com.example.psicologic.feature_emotions.presentation.emotions.EmotionsScreen
import com.example.psicologic.feature_questions.presentation.answer.AnswerScreen
import com.example.psicologic.feature_questions.presentation.questions.QuestionsScreen
import com.example.psicologic.feature_recommendations.presentation.recommendations.RecommendationsScreen
import com.example.psicologic.feature_relaxation_sounds.domain.usecases.GetRelaxationSoundsUseCase
import com.example.psicologic.feature_relaxation_sounds.domain.usecases.PlayRelaxationSoundUseCase
import com.example.psicologic.feature_relaxation_sounds.domain.usecases.StopRelaxationSoundUseCase
import com.example.psicologic.feature_relaxation_sounds.presentation.relaxation_player.RelaxationPlayerScreen
import com.example.psicologic.feature_relaxation_sounds.presentation.relaxation_player.RelaxationPlayerViewModel

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
    val context = LocalContext.current

    // Routes that show bottom navigation
    val bottomNavRoutes = listOf(
        NavigationRoutes.EmotionsTab.route,
        NavigationRoutes.QuestionsTab.route,
        NavigationRoutes.AnswersTab.route,
        NavigationRoutes.RecommendationsTab.route,
        NavigationRoutes.RelaxationTab.route  // Ruta para el tab de relajación
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
        ),
        BottomNavItem(
            title = "Relajación",
            icon = AppIcons.BottomNav.Relaxation,
            route = NavigationRoutes.RelaxationTab
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

            // Tab para la sección de relajación - CORREGIDO: mostrar directamente la pantalla
            composable(NavigationRoutes.RelaxationTab.route) {
                val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
                val viewModel = remember {
                    ViewModelProvider(
                        owner = viewModelStoreOwner,
                        factory = ViewModelFactory.createFactory {
                            RelaxationPlayerViewModel(
                                getRelaxationSoundsUseCase = GetRelaxationSoundsUseCase(
                                    repository = ServiceLocator.provideRelaxationRepository(context)
                                ),
                                playRelaxationSoundUseCase = PlayRelaxationSoundUseCase(
                                    repository = ServiceLocator.provideRelaxationRepository(context)
                                ),
                                stopRelaxationSoundUseCase = StopRelaxationSoundUseCase()
                            )
                        }
                    )[RelaxationPlayerViewModel::class.java]
                }

                RelaxationPlayerScreen(viewModel = viewModel)
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

            // Mantenemos esta ruta para acceso directo desde otras partes de la app si es necesario
            composable(NavigationRoutes.RelaxationPlayer.route) {
                val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
                val viewModel = remember {
                    ViewModelProvider(
                        owner = viewModelStoreOwner,
                        factory = ViewModelFactory.createFactory {
                            RelaxationPlayerViewModel(
                                getRelaxationSoundsUseCase = GetRelaxationSoundsUseCase(
                                    repository = ServiceLocator.provideRelaxationRepository(context)
                                ),
                                playRelaxationSoundUseCase = PlayRelaxationSoundUseCase(
                                    repository = ServiceLocator.provideRelaxationRepository(context)
                                ),
                                stopRelaxationSoundUseCase = StopRelaxationSoundUseCase()
                            )
                        }
                    )[RelaxationPlayerViewModel::class.java]
                }

                RelaxationPlayerScreen(viewModel = viewModel)
            }
        }
    }
}