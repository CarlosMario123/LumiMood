package com.example.psicologic.feature_auth.data.models


import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val token: String,
    val role: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)