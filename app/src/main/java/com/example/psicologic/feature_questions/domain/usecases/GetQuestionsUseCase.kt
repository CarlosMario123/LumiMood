package com.example.psicologic.feature_questions.domain.usecases

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_questions.domain.models.Question
import com.example.psicologic.feature_questions.domain.repository.QuestionsRepository

/**
 * Caso de uso para obtener la lista de preguntas
 */
class GetQuestionsUseCase(private val questionsRepository: QuestionsRepository) {

    /**
     * Ejecuta el caso de uso para obtener todas las preguntas
     */
    suspend operator fun invoke(): Result<List<Question>> {
        return questionsRepository.getQuestions()
    }

    /**
     * Obtiene una pregunta específica por su ID
     */
    suspend fun getQuestionById(id: Int): Result<Question> {
        return questionsRepository.getQuestionById(id)
    }
}
