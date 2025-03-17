package com.example.psicologic.feature_questions.domain.usecases

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_questions.domain.models.Question
import com.example.psicologic.feature_questions.domain.repository.QuestionsRepository

/**
 * Caso de uso para añadir una nueva pregunta
 */
class AddQuestionUseCase(private val questionsRepository: QuestionsRepository) {

    /**
     * Ejecuta el caso de uso para añadir una nueva pregunta
     */
    suspend operator fun invoke(question: String): Result<Question> {
        // Validar input
        if (question.isBlank()) {
            return Result.Error(Exception("La pregunta no puede estar vacía"))
        }

        return questionsRepository.addQuestion(question)
    }
}