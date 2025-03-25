// RelaxationSoundEntity.kt
package com.example.psicologic.feature_relaxation_sounds.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "relaxation_sounds")
data class RelaxationSoundEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val resourcePath: String,
    val durationSeconds: Int,
    val category: String
)