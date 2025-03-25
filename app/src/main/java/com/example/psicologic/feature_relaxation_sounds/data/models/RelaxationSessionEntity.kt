package com.example.psicologic.feature_relaxation_sounds.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "relaxation_sessions")
data class RelaxationSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val soundId: Int,
    val startTime: Date,
    val durationMinutes: Int,
    val completed: Boolean
)