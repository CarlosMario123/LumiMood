// SaveRelaxationSessionUseCase.kt
package com.example.psicologic.feature_relaxation_sounds.domain.usecases

import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSession
import com.example.psicologic.feature_relaxation_sounds.domain.repository.RelaxationRepository
import java.util.Date

class SaveRelaxationSessionUseCase(
    private val repository: RelaxationRepository
) {
    suspend operator fun invoke(soundId: Int, durationMinutes: Int, completed: Boolean = true) {
        val session = RelaxationSession(
            soundId = soundId,
            startTime = Date(),
            durationMinutes = durationMinutes,
            completed = completed
        )
        repository.saveSession(session)
    }
}