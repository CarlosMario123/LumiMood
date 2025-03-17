package com.example.psicologic.core.data.local.remote

import android.content.Context

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit para hacer peticiones a la API
 */
object RetrofitClient {

    // URL base de la API
    private const val BASE_URL = "http://13.216.177.187:3000/api/"

    // Timeout para las peticiones en segundos
    private const val TIMEOUT = 30L

    // Instancia única del AuthInterceptor
    private val authInterceptor = AuthInterceptor.getInstance()

    /**
     * Actualiza el token de autenticación
     */
    fun updateToken(token: String) {
        authInterceptor.setToken(token)
    }

    /**
     * Crea una instancia del servicio API usando Retrofit
     */
    fun createApiService(): ApiService {
        val retrofit = createRetrofit()
        return retrofit.create(ApiService::class.java)
    }

    /**
     * Crea una instancia configurada de Retrofit
     */
    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Crea un cliente OkHttp con interceptores y timeouts configurados
     */
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
}