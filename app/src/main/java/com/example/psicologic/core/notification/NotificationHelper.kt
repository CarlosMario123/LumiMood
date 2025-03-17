package com.example.psicologic.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.psicologic.MainActivity
import com.example.psicologic.R

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Main channel for general notifications
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General notifications from LumiMood"
                enableLights(true)
                enableVibration(true)
            }

            // Channel for emotion tracking reminders
            val emotionChannel = NotificationChannel(
                CHANNEL_EMOTIONS,
                "Emotion Tracking",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to track your emotions"
                enableLights(true)
                enableVibration(true)
            }

            // Channel for question responses
            val questionsChannel = NotificationChannel(
                CHANNEL_QUESTIONS,
                "Question Responses",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about your questions and answers"
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(listOf(generalChannel, emotionChannel, questionsChannel))
        }
    }

    fun showNotification(title: String, message: String, data: Map<String, String> = emptyMap()) {
        val channelId = when (data["type"]) {
            "emotion_reminder" -> CHANNEL_EMOTIONS
            "question_response" -> CHANNEL_QUESTIONS
            else -> CHANNEL_GENERAL
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // Add data for deep linking if needed
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            getNotificationId(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Make sure to add this resource
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(getNotificationId(), notification)
    }

    private fun getNotificationId(): Int {
        return System.currentTimeMillis().toInt()
    }

    companion object {
        const val CHANNEL_GENERAL = "channel_general"
        const val CHANNEL_EMOTIONS = "channel_emotions"
        const val CHANNEL_QUESTIONS = "channel_questions"
    }
}
