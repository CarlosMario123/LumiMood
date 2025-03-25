package com.example.psicologic.feature_relaxation_sounds.domain.usecases

import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSound
import com.example.psicologic.feature_relaxation_sounds.domain.repository.RelaxationRepository
import kotlinx.coroutines.flow.Flow

class GetRelaxationSoundsUseCase(
    private val repository: RelaxationRepository
) {
    operator fun invoke(): Flow<List<RelaxationSound>> {
        return repository.getAllSounds()
    }
}