
package com.example.psicologic.feature_relaxation_sounds.domain.repository

import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSound
import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSession
import kotlinx.coroutines.flow.Flow

interface RelaxationRepository {
    fun getAllSounds(): Flow<List<RelaxationSound>>
    suspend fun getSoundById(soundId: Int): RelaxationSound?
    suspend fun saveSession(session: RelaxationSession): Long
    fun getAllSessions(): Flow<List<RelaxationSession>>
}