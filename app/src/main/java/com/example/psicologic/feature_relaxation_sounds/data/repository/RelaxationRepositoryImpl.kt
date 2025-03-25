// RelaxationRepositoryImpl.kt
package com.example.psicologic.feature_relaxation_sounds.data.repository

import com.example.psicologic.feature_relaxation_sounds.data.local.RelaxationSoundDao
import com.example.psicologic.feature_relaxation_sounds.data.models.RelaxationSessionEntity
import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSound
import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSession
import com.example.psicologic.feature_relaxation_sounds.domain.repository.RelaxationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RelaxationRepositoryImpl(
    private val dao: RelaxationSoundDao
) : RelaxationRepository {

    override fun getAllSounds(): Flow<List<RelaxationSound>> {
        return dao.getAllSounds().map { entities ->
            entities.map { entity ->
                RelaxationSound(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    resourcePath = entity.resourcePath,
                    durationSeconds = entity.durationSeconds,
                    category = entity.category
                )
            }
        }
    }

    override suspend fun getSoundById(soundId: Int): RelaxationSound? {
        return dao.getSoundById(soundId)?.let { entity ->
            RelaxationSound(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                resourcePath = entity.resourcePath,
                durationSeconds = entity.durationSeconds,
                category = entity.category
            )
        }
    }

    override suspend fun saveSession(session: RelaxationSession): Long {
        val entity = RelaxationSessionEntity(
            id = session.id,
            soundId = session.soundId,
            startTime = session.startTime,
            durationMinutes = session.durationMinutes,
            completed = session.completed
        )
        return dao.saveSession(entity)
    }

    override fun getAllSessions(): Flow<List<RelaxationSession>> {
        return dao.getAllSessions().map { entities ->
            entities.map { entity ->
                RelaxationSession(
                    id = entity.id,
                    soundId = entity.soundId,
                    startTime = entity.startTime,
                    durationMinutes = entity.durationMinutes,
                    completed = entity.completed
                )
            }
        }
    }
}