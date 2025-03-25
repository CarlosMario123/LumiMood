// ResumeRelaxationSoundUseCase.kt
package com.example.psicologic.feature_relaxation_sounds.domain.usecases

import android.content.Context
import android.content.Intent
import com.example.psicologic.feature_relaxation_sounds.presentation.service.RelaxationSoundService

class ResumeRelaxationSoundUseCase {
    operator fun invoke(context: Context) {
        val intent = Intent(context, RelaxationSoundService::class.java).apply {
            action = RelaxationSoundService.ACTION_RESUME
        }
        context.startService(intent)
    }
}