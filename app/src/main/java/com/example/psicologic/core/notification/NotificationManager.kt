// NotificationManager.kt
package com.example.psicologic.core.notification

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NotificationManager(private val context: Context) {

    private val notificationHelper = NotificationHelper(context)

    /**
     * Obtiene el token FCM actual para notificaciones
     * Espera correctamente a que el token esté disponible
     */
    suspend fun getFcmToken(): String {
        return withContext(Dispatchers.IO) {
            try {
                // Usamos await() para esperar a que el token esté disponible
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d("FCM", "Token obtenido: $token")
                token
            } catch (e: Exception) {
                Log.e("FCM", "Error al obtener token FCM", e)
                ""  // Retorna un string vacío en caso de error
            }
        }
    }

    /**
     * Suscribe a un tema específico para notificaciones dirigidas
     */
    suspend fun subscribeToTopic(topic: String): Boolean {
        return try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
            true
        } catch (e: Exception) {
            Log.e("FCM", "Error al suscribirse al tema: $topic", e)
            false
        }
    }

    /**
     * Muestra una notificación local
     */
    fun showLocalNotification(title: String, message: String, data: Map<String, String> = emptyMap()) {
        notificationHelper.showNotification(title, message, data)
    }
}