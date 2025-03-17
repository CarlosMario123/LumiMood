package com.example.psicologic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.psicologic.core.di.ServiceLocator
import com.example.psicologic.core.presentation.navigation.SetupAppNavigation
import com.example.psicologic.core.presentation.theme.PsicologicTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get FCM token and store it for later use with login
        getFcmToken()

        setContent {
            PsicologicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    SetupAppNavigation(deepLinkIntent = intent)
                }
            }
        }
    }

    private fun getFcmToken() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val notificationManager = ServiceLocator.provideNotificationManager(applicationContext)
                val token = notificationManager.getFcmToken()

                ServiceLocator
                    .provideSharedPreferencesManager(applicationContext)
                    .saveFcmToken(token)
            } catch (e: Exception) {

            }
        }
    }
}