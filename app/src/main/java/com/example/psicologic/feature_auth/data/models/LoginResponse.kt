package com.example.psicologic.feature_auth.data.models

data class LoginResponse (
    val email: String,
    val token:String,
    val fcmToken:String
)