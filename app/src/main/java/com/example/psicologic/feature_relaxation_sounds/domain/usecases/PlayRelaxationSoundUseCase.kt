// PlayRelaxationSoundUseCase.kt
package com.example.psicologic.feature_relaxation_sounds.domain.usecases

import android.content.Context
import android.content.Intent
import com.example.psicologic.feature_relaxation_sounds.domain.repository.RelaxationRepository
import com.example.psicologic.feature_relaxation_sounds.presentation.service.RelaxationSoundService

class PlayRelaxationSoundUseCase(
    private val repository: RelaxationRepository
) {
    suspend operator fun invoke(context: Context, soundId: Int) {
        val sound = repository.getSoundById(soundId)
        sound?.let {
            val intent = Intent(context, RelaxationSoundService::class.java).apply {
                action = RelaxationSoundService.ACTION_PLAY
                putExtra(RelaxationSoundService.EXTRA_SOUND_ID, soundId)
            }
            context.startForegroundService(intent)
        }
    }
}