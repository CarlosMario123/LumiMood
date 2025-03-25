package com.example.psicologic.feature_relaxation_sounds.domain.models

import java.util.Date

data class RelaxationSession(
    val id: Int = 0,
    val soundId: Int,
    val startTime: Date,
    val durationMinutes: Int,
    val completed: Boolean
)