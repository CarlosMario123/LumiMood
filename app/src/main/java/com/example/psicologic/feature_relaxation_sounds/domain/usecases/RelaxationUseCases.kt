// RelaxationUseCases.kt
package com.example.psicologic.feature_relaxation_sounds.domain.usecases

data class RelaxationUseCases(
    val getRelaxationSounds: GetRelaxationSoundsUseCase,
    val getRelaxationSoundById: GetRelaxationSoundByIdUseCase,
    val playRelaxationSound: PlayRelaxationSoundUseCase,
    val pauseRelaxationSound: PauseRelaxationSoundUseCase,
    val resumeRelaxationSound: ResumeRelaxationSoundUseCase,
    val stopRelaxationSound: StopRelaxationSoundUseCase,
    val saveRelaxationSession: SaveRelaxationSessionUseCase,
    val getRelaxationSessions: GetRelaxationSessionsUseCase
)