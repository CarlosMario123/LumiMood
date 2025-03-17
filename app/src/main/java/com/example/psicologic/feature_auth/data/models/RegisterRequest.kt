package com.example.psicologic.feature_auth.data.models

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "user",
    val fcmToken: String
)
