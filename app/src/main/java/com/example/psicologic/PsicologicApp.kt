// PsicologicApp.kt
package com.example.psicologic

import android.app.Application
import android.util.Log
import com.example.psicologic.core.di.ServiceLocator
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class PsicologicApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase antes de cualquier operación con FCM
        initializeFirebase()

        // Inicializar ServiceLocator
        ServiceLocator.initialize(this)
    }

    private fun initializeFirebase() {
        try {
            // Inicializar Firebase
            FirebaseApp.initializeApp(this)

            // Registrar para recibir notificaciones (opcional)
            FirebaseMessaging.getInstance().isAutoInitEnabled = true

            // Verificar inicialización
            Log.d("Firebase", "Firebase inicializado correctamente: ${FirebaseApp.getInstance().name}")
        } catch (e: Exception) {
            Log.e("Firebase", "Error al inicializar Firebase", e)
        }
    }
}