package com.example.psicologic.feature_relaxation_sounds.domain.models

data class RelaxationSound(
    val id: Int,
    val name: String,
    val description: String,
    val resourcePath: String,
    val durationSeconds: Int,
    val category: String
)
