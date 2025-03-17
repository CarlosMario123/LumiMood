// ApiService.kt
package com.example.psicologic.core.data.local.remote

import com.example.psicologic.feature_auth.data.models.LoginRequest
import com.example.psicologic.feature_auth.data.models.LoginResponse
import com.example.psicologic.feature_auth.data.models.RegisterRequest
import com.example.psicologic.feature_auth.data.models.UserResponse
import com.example.psicologic.feature_emotions.data.models.EmotionRequest
import com.example.psicologic.feature_questions.data.models.AnswerRequest
import com.example.psicologic.feature_questions.data.models.QuestionRequest
import com.example.psicologic.feature_questions.data.models.QuestionResponse
import com.example.psicologic.feature_recommendations.data.models.RecommendationRequest
import com.example.psicologic.feature_recommendations.data.models.RecommendationResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interfaz que define los endpoints de la API
 */
interface ApiService {

    // ==================
    // AUTENTICACIÓN
    // ==================

    /**
     * Inicio de sesión de usuario
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Registro de nuevo usuario
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    /**
     * Actualizar token FCM para notificaciones
     */
    @PUT("auth/fcm-token")
    suspend fun updateFcmToken(@Body request: Map<String, String>): Response<Unit>

    /**
     * Obtener perfil del usuario
     */
    @GET("auth/profile")
    suspend fun getProfile(): Response<UserResponse>

    // ==================
    // EMOCIONES
    // ==================

    /**
     * Registrar una emoción
     */
    @POST("emotions")
    suspend fun registerEmotion(@Body request: EmotionRequest): Response<Unit>

    /**
     * Obtener historial de emociones
     */
    @GET("emotions")
    suspend fun getEmotionHistory(): Response<List<EmotionResponse>>

    /**
     * Obtener la última emoción registrada
     */
    @GET("emotions/last")
    suspend fun getLastEmotion(): Response<EmotionResponse>
    // ==================
    // PREGUNTAS
    // ==================

    /**
     * Obtiene lista de preguntas
     */
    @GET("messages")
    suspend fun getQuestions(): Response<List<QuestionResponse>>

    /**
     * Obtiene una pregunta por ID
     */
    @GET("messages/{id}")
    suspend fun getQuestionById(@Path("id") id: Int): Response<QuestionResponse>

    /**
     * Crear una nueva pregunta
     */
    @POST("messages")
    suspend fun addQuestion(@Body request: QuestionRequest): Response<QuestionResponse>

    /**
     * Responder a una pregunta
     */
    @POST("messages/answer")
    suspend fun answerQuestion(@Body request: AnswerRequest): Response<Unit>

    // ==================
    // RECOMENDACIONES
    // ==================

    /**
     * Obtener lista de recomendaciones
     */
    @GET("recommendations")
    suspend fun getRecommendations(): Response<List<RecommendationResponse>>

    /**
     * Obtener una recomendación por ID
     */
    @GET("recommendations/{id}")
    suspend fun getRecommendationById(@Path("id") id: Int): Response<RecommendationResponse>

    /**
     * Crear una nueva recomendación
     */
    @POST("recommendations")
    suspend fun addRecommendation(@Body request: RecommendationRequest): Response<RecommendationResponse>
}

/**
 * Respuesta de una emoción desde la API
 */
data class EmotionResponse(
    val id: String,
    val emotion: String,
    val color: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("user_id")
    val userId: Int? = null
)