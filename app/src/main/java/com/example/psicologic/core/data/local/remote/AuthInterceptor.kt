package com.example.psicologic.core.data.local.remote

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor para añadir el token de autenticación a las peticiones
 */
class AuthInterceptor(private val context: Context? = null) : Interceptor {

    private var token: String? = null

    /**
     * Permite establecer el token manualmente
     */
    fun setToken(newToken: String) {
        token = newToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()


        val url = originalRequest.url.toString()


        if (url.contains("login") || url.contains("register")) {
            return chain.proceed(originalRequest)
        }

        // Si tenemos token, lo usamos
        val authToken = token

        if (authToken != null) {
            // Añadimos el token a la petición
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()

            return chain.proceed(authenticatedRequest)
        }

        // Si no hay token, procedemos con la petición original
        return chain.proceed(originalRequest)
    }

    companion object {
        private var instance: AuthInterceptor? = null

        fun getInstance(context: Context? = null): AuthInterceptor {
            if (instance == null) {
                instance = AuthInterceptor(context)
            }
            return instance!!
        }
    }
}