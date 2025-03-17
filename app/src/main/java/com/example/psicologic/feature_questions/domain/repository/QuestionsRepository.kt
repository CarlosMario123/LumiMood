package com.example.psicologic.feature_questions.domain.repository

import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_questions.domain.models.Question

/**
 * Interfaz del repositorio para operaciones con preguntas
 */
interface QuestionsRepository {
    /**
     * Obtiene la lista de preguntas
     */
    suspend fun getQuestions(): Result<List<Question>>

    /**
     * Obtiene una pregunta específica por su ID
     */
    suspend fun getQuestionById(id: Int): Result<Question>

    /**
     * Añade una nueva pregunta
     */
    suspend fun addQuestion(question: String): Result<Question>

    /**
     * Responde a una pregunta
     */
    suspend fun answerQuestion(questionId: Int, answer: String): Result<Boolean>
}