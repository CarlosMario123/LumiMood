package com.example.psicologic.feature_relaxation_sounds.domain.usecases

import com.example.psicologic.feature_relaxation_sounds.domain.models.RelaxationSound
import com.example.psicologic.feature_relaxation_sounds.domain.repository.RelaxationRepository

class GetRelaxationSoundByIdUseCase(
    private val repository: RelaxationRepository
) {
    suspend operator fun invoke(id: Int): RelaxationSound? {
        return repository.getSoundById(id)
    }
}