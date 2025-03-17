package com.example.psicologic.core.notification

import android.util.Log
import com.example.psicologic.core.di.ServiceLocator
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMService : FirebaseMessagingService() {

    private val notificationHelper by lazy { NotificationHelper(this) }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
      Log.d("fcm","llego")
        // Check if message contains a notification payload
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "LumiMood"
            val body = notification.body ?: ""

            // Create and show the notification
            notificationHelper.showNotification(title, body, remoteMessage.data)
        }

        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            // Process the data payload if needed
            handleDataPayload(remoteMessage.data)
        }
    }

    private fun handleDataPayload(data: Map<String, String>) {
        // Example: Process different types of notifications based on type in data
        when (data["type"]) {
            "new_question" -> {
                // Handle new question notification
            }
            "answer_received" -> {
                // Handle answer received notification
            }
            "recommendation" -> {
                // Handle recommendation notification
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Send the token to your server
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        // Use coroutine to send token to server
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Store token locally
                ServiceLocator.provideSharedPreferencesManager(applicationContext)
                    .saveFcmToken(token)

                // If user is logged in, update token on server
                val authRepository = ServiceLocator.provideAuthRepository()
                val userId = ServiceLocator.provideSharedPreferencesManager(applicationContext)
                    .getUserId()

                if (userId != null) {
                    // Update FCM token on server
                    val updateFcmTokenUseCase = ServiceLocator.provideUpdateFcmTokenUseCase()
                    updateFcmTokenUseCase(token)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send FCM token to server", e)
            }
        }
    }

    companion object {
        private const val TAG = "PsicologicFCM"
    }
}