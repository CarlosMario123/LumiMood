package com.example.psicologic.core.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.psicologic.core.data.local.remote.RetrofitClient

/**
 * Maneja las operaciones de almacenamiento local usando SharedPreferences
 */
class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )

    /**
     * Guarda la información de sesión del usuario
     */
    fun saveUserSession(userId: Int, token: String, name: String, email: String) {
        sharedPreferences.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()

        // Actualiza el token en RetrofitClient para futuras peticiones
        RetrofitClient.updateToken(token)
    }

    /**
     * Guarda el token FCM para notificaciones
     */
    fun saveFcmToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_FCM_TOKEN, token)
            .apply()
    }

    /**
     * Obtiene el token FCM guardado
     */
    fun getFcmToken(): String? {
        return sharedPreferences.getString(KEY_FCM_TOKEN, null)
    }

    /**
     * Verifica si el usuario está logueado
     */
    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Obtiene el token de autenticación
     */
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    /**
     * Obtiene el ID del usuario
     */
    fun getUserId(): Int? {
        val userId = sharedPreferences.getInt(KEY_USER_ID, -1)
        return if (userId != -1) userId else null
    }

    /**
     * Obtiene el nombre del usuario
     */
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    /**
     * Obtiene el email del usuario
     */
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Actualiza el nombre del usuario
     */
    fun updateUserName(name: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    /**
     * Actualiza el email del usuario
     */
    fun updateUserEmail(email: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    /**
     * Actualiza el token de autenticación
     */
    fun updateAuthToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_TOKEN, token)
            .apply()

        // Actualiza el token en RetrofitClient
        RetrofitClient.updateToken(token)
    }

    /**
     * Limpia los datos de sesión (logout)
     */
    fun clearUserSession() {
        sharedPreferences.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_TOKEN)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_EMAIL)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()

        // Limpiar el token en RetrofitClient
        RetrofitClient.updateToken("")
    }

    /**
     * Guarda la última fecha de sincronización
     */
    fun saveLastSyncTime(timestamp: Long) {
        sharedPreferences.edit()
            .putLong(KEY_LAST_SYNC, timestamp)
            .apply()
    }

    /**
     * Obtiene la última fecha de sincronización
     */
    fun getLastSyncTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_SYNC, 0)
    }

    /**
     * Guarda la configuración de notificaciones
     */
    fun saveNotificationSettings(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
            .apply()
    }

    /**
     * Verifica si las notificaciones están habilitadas
     */
    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    /**
     * Guarda el tema de la aplicación (claro/oscuro)
     */
    fun saveThemeMode(isDarkMode: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_MODE, isDarkMode)
            .apply()
    }

    /**
     * Obtiene el tema de la aplicación
     */
    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    /**
     * Limpia todas las preferencias
     */
    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "com.example.psicologic.prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_LAST_SYNC = "last_sync_time"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DARK_MODE = "dark_mode"
    }
}