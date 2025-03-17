// AuthRepositoryImpl.kt
package com.example.psicologic.feature_auth.data.repository

import com.example.psicologic.core.data.local.SharedPreferencesManager
import com.example.psicologic.core.data.local.remote.ApiService

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_auth.data.models.LoginRequest
import com.example.psicologic.feature_auth.data.models.RegisterRequest
import com.example.psicologic.feature_auth.domain.models.User
import com.example.psicologic.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación del repositorio de autenticación
 */
class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val sharedPreferencesManager: SharedPreferencesManager
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                val userResponse = response.body()!!
                // Mapear a modelo de dominio
                val user = User(
                    id = 1,
                    name = "",
                    email = userResponse.email,
                    token = userResponse.token,
                    role = "user"
                )

                // Guardar datos de usuario en SharedPreferences
                sharedPreferencesManager.saveUserSession(
                    userId = user.id,
                    token = user.token,
                    name = user.name,
                    email = user.email
                )

                Result.Success(user)
            } else {
                Result.Error(Exception("Error al iniciar sesión: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        fcmToken: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(
                name = name,
                email = email,
                password = password,
                role = "user",
                fcmToken = fcmToken
            )

            val response = apiService.register(request)

            if (response.isSuccessful) {
                Result.Success(true)
            } else {
                Result.Error(Exception("Error en el registro: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateFcmToken(token: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Solo enviar token al servidor si el usuario está logueado
            if (sharedPreferencesManager.isUserLoggedIn()) {
                val response = apiService.updateFcmToken(mapOf("fcmToken" to token))

                if (response.isSuccessful) {
                    // Actualizar almacenamiento local
                    sharedPreferencesManager.saveFcmToken(token)
                    Result.Success(true)
                } else {
                    Result.Error(Exception("Error al actualizar token: ${response.message()}"))
                }
            } else {
                // Solo guardar localmente si no está logueado
                sharedPreferencesManager.saveFcmToken(token)
                Result.Success(true)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun logout(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Llamar a logout API si es necesario
            // val response = apiService.logout()

            // Limpiar datos de sesión de usuario
            sharedPreferencesManager.clearUserSession()

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}