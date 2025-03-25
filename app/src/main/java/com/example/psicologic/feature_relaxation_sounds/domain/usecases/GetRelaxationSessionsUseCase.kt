// GetRelaxationSessionsUseCase.kt
package com.example.psicologic.feature_relaxation_sounds.domain.usecases

import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSession
import com.example.psicologic.feature_relaxation_sounds.domain.repository.RelaxationRepository
import kotlinx.coroutines.flow.Flow

class GetRelaxationSessionsUseCase(
    private val repository: RelaxationRepository
) {
    operator fun invoke(): Flow<List<RelaxationSession>> {
        return repository.getAllSessions()
    }
}