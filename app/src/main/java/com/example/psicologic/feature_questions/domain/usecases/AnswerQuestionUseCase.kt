package com.example.psicologic.feature_questions.domain.usecases

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_questions.domain.repository.QuestionsRepository

/**
 * Caso de uso para responder a una pregunta
 */
class AnswerQuestionUseCase(private val questionsRepository: QuestionsRepository) {

    /**
     * Ejecuta el caso de uso para responder a una pregunta
     */
    suspend operator fun invoke(questionId: Int, answer: String): Result<Boolean> {
        // Validar inputs
        if (questionId <= 0) {
            return Result.Error(Exception("ID de pregunta inválido"))
        }

        if (answer.isBlank()) {
            return Result.Error(Exception("La respuesta no puede estar vacía"))
        }

        return questionsRepository.answerQuestion(questionId, answer)
    }
}