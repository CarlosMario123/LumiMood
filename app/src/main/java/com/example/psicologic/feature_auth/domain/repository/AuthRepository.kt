// AuthRepository.kt
package com.example.psicologic.feature_auth.domain.repository

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_auth.domain.models.User

/**
 * Interfaz de repositorio para operaciones de autenticación
 */
interface AuthRepository {
    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Registra un nuevo usuario con fcmToken
     */
    suspend fun register(name: String, email: String, password: String, fcmToken: String): Result<Boolean>

    /**
     * Actualiza el token FCM para notificaciones
     */
    suspend fun updateFcmToken(token: String): Result<Boolean>

    /**
     * Cierra la sesión del usuario
     */
    suspend fun logout(): Result<Boolean>
}