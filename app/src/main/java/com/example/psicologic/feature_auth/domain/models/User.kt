package com.example.psicologic.feature_auth.domain.models

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val token: String,
    val role: String
)