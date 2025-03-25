package com.example.psicologic.core.di

import android.content.Context
import com.example.psicologic.core.data.local.SharedPreferencesManager
import com.example.psicologic.core.data.local.remote.ApiService
import com.example.psicologic.core.data.local.remote.RetrofitClient

import com.example.psicologic.core.notification.NotificationManager
import com.example.psicologic.feature_auth.data.repository.AuthRepositoryImpl
import com.example.psicologic.feature_auth.domain.repository.AuthRepository
import com.example.psicologic.feature_auth.domain.usecases.LoginUseCase
import com.example.psicologic.feature_auth.domain.usecases.RegisterUseCase
import com.example.psicologic.feature_auth.domain.usecases.UpdateFcmTokenUseCase
import com.example.psicologic.feature_auth.presentation.login.LoginViewModel
import com.example.psicologic.feature_auth.presentation.register.RegisterViewModel
import com.example.psicologic.feature_emotions.data.repository.EmotionsRepositoryImpl
import com.example.psicologic.feature_emotions.domain.repository.EmotionsRepository
import com.example.psicologic.feature_emotions.domain.usecases.GetEmotionHistoryUseCase
import com.example.psicologic.feature_emotions.domain.usecases.RegisterEmotionUseCase
import com.example.psicologic.feature_emotions.presentation.emotions.EmotionsViewModel
import com.example.psicologic.feature_questions.data.repository.QuestionsRepositoryImpl
import com.example.psicologic.feature_questions.domain.repository.QuestionsRepository
import com.example.psicologic.feature_questions.domain.usecases.AddQuestionUseCase
import com.example.psicologic.feature_questions.domain.usecases.AnswerQuestionUseCase
import com.example.psicologic.feature_questions.domain.usecases.GetQuestionsUseCase
import com.example.psicologic.feature_questions.presentation.answer.AnswerViewModel
import com.example.psicologic.feature_questions.presentation.questions.QuestionsViewModel
import com.example.psicologic.feature_recommendations.data.repository.RecommendationsRepositoryImpl
import com.example.psicologic.feature_recommendations.domain.repository.RecommendationsRepository
import com.example.psicologic.feature_recommendations.domain.usecase.AddRecommendationUseCase
import com.example.psicologic.feature_recommendations.domain.usecase.GetRecommendationsUseCase

import com.example.psicologic.feature_recommendations.presentation.recommendations.RecommendationsViewModel
import com.example.psicologic.feature_relaxation_sounds.data.local.RelaxationDatabase
import com.example.psicologic.feature_relaxation_sounds.data.repository.RelaxationRepositoryImpl
import com.example.psicologic.feature_relaxation_sounds.domain.repository.RelaxationRepository

/**
 * ServiceLocator para la inyección de dependencias manual
 * Reemplaza Hilt/Dagger proporcionando dependencias de forma manual
 */
object ServiceLocator {
    private lateinit var applicationContext: Context

    /**
     * Inicializar el ServiceLocator con el contexto de la aplicación
     */
    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    // ============================
    // SERVICIOS PRINCIPALES
    // ============================

    // API Service
    private val apiService: ApiService by lazy {
        RetrofitClient.createApiService()
    }

    // Notification Manager
    fun provideNotificationManager(context: Context): NotificationManager {
        return NotificationManager(context)
    }

    // Shared Preferences
    fun provideSharedPreferencesManager(context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }

    // ============================
    // AUTENTICACIÓN
    // ============================

    // Auth Repository
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl(
            apiService = apiService,
            sharedPreferencesManager = provideSharedPreferencesManager(applicationContext)
        )
    }

    // Auth Use Cases
    fun provideLoginUseCase(): LoginUseCase {
        return LoginUseCase(provideAuthRepository())
    }

    fun provideRegisterUseCase(): RegisterUseCase {
        return RegisterUseCase(provideAuthRepository())
    }

    fun provideUpdateFcmTokenUseCase(): UpdateFcmTokenUseCase {
        return UpdateFcmTokenUseCase(provideAuthRepository())
    }

    // Auth ViewModels
    fun provideLoginViewModel(context: Context): LoginViewModel {
        return LoginViewModel(
            loginUseCase = provideLoginUseCase(),
            notificationManager = provideNotificationManager(context)
        )
    }

    fun provideRegisterViewModel(context: Context): RegisterViewModel {
        return RegisterViewModel(
            registerUseCase = provideRegisterUseCase(),
            notificationManager = provideNotificationManager(context)
        )
    }

    // ============================
    // EMOCIONES
    // ============================

    // Emotions Repository
    fun provideEmotionsRepository(): EmotionsRepository {
        return EmotionsRepositoryImpl(apiService)
    }

    // Emotions Use Cases
    fun provideRegisterEmotionUseCase(): RegisterEmotionUseCase {
        return RegisterEmotionUseCase(provideEmotionsRepository())
    }

    fun provideGetEmotionHistoryUseCase(): GetEmotionHistoryUseCase {
        return GetEmotionHistoryUseCase(provideEmotionsRepository())
    }

    // Emotions ViewModel
    fun provideEmotionsViewModel(context: Context): EmotionsViewModel {
        return EmotionsViewModel(
            registerEmotionUseCase = provideRegisterEmotionUseCase(),
            getEmotionHistoryUseCase = provideGetEmotionHistoryUseCase()
        )
    }

    // ============================
// PREGUNTAS Y RESPUESTAS
// ============================

    // Questions Repository
    fun provideQuestionsRepository(): QuestionsRepository {
        return QuestionsRepositoryImpl(apiService)
    }

    // Questions Use Cases
    fun provideGetQuestionsUseCase(): GetQuestionsUseCase {
        return GetQuestionsUseCase(provideQuestionsRepository())
    }

    fun provideAddQuestionUseCase(): AddQuestionUseCase {
        return AddQuestionUseCase(provideQuestionsRepository())
    }

    fun provideAnswerQuestionUseCase(): AnswerQuestionUseCase {
        return AnswerQuestionUseCase(provideQuestionsRepository())
    }

    // Questions ViewModels
    fun provideQuestionsViewModel(context: Context): QuestionsViewModel {
        return QuestionsViewModel(
            getQuestionsUseCase = provideGetQuestionsUseCase(),
            addQuestionUseCase = provideAddQuestionUseCase()
        )
    }

    fun provideAnswerViewModel(questionId: Int, context: Context): AnswerViewModel {
        return AnswerViewModel(
            questionId = questionId,
            getQuestionsUseCase = provideGetQuestionsUseCase(),
            answerQuestionUseCase = provideAnswerQuestionUseCase()
        )
    }
    // ============================
// RECOMENDACIONES
// ============================

    // Recommendations Repository
    fun provideRecommendationsRepository(): RecommendationsRepository {
        return RecommendationsRepositoryImpl(apiService)
    }

    // Recommendations Use Cases
    fun provideGetRecommendationsUseCase(): GetRecommendationsUseCase {
        return GetRecommendationsUseCase(provideRecommendationsRepository())
    }

    fun provideAddRecommendationUseCase(): AddRecommendationUseCase {
        return AddRecommendationUseCase(provideRecommendationsRepository())
    }

    // Recommendations ViewModel
    fun provideRecommendationsViewModel(context: Context): RecommendationsViewModel {
        return RecommendationsViewModel(
            getRecommendationsUseCase = provideGetRecommendationsUseCase(),
            addRecommendationUseCase = provideAddRecommendationUseCase()
        )
    }
    fun provideRelaxationRepository(context: Context): RelaxationRepository {
        return RelaxationRepositoryImpl(
            dao = RelaxationDatabase.getDatabase(context).relaxationSoundDao()
        )
    }
}