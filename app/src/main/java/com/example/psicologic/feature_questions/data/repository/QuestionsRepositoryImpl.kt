// QuestionsRepositoryImpl.kt
package com.example.psicologic.feature_questions.data.repository

import com.example.psicologic.core.data.local.remote.ApiService
import com.example.psicologic.core.domain.models.Result
import com.example.psicologic.feature_questions.data.models.AnswerRequest
import com.example.psicologic.feature_questions.data.models.QuestionRequest
import com.example.psicologic.feature_questions.domain.models.Question
import com.example.psicologic.feature_questions.domain.repository.QuestionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Implementación del repositorio de preguntas
 */
class QuestionsRepositoryImpl(
    private val apiService: ApiService
) : QuestionsRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

    override suspend fun getQuestions(): Result<List<Question>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getQuestions()

                if (response.isSuccessful && response.body() != null) {
                    val questions = response.body()!!.map { questionResponse ->
                        // Convertir fecha de string a Date
                        val timestamp = try {
                            dateFormat.parse(questionResponse.createdAt) ?: Date()
                        } catch (e: Exception) {
                            Date()
                        }

                        Question(
                            id = questionResponse.id,
                            question = questionResponse.question,
                            response = questionResponse.response,
                            timestamp = timestamp,
                            hasAnswer = questionResponse.response != null
                        )
                    }
                    Result.Success(questions)
                } else {
                    Result.Error(Exception("Error al obtener preguntas: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getQuestionById(id: Int): Result<Question> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getQuestionById(id)

                if (response.isSuccessful && response.body() != null) {
                    val questionResponse = response.body()!!

                    // Convertir fecha de string a Date
                    val timestamp = try {
                        dateFormat.parse(questionResponse.createdAt) ?: Date()
                    } catch (e: Exception) {
                        Date()
                    }

                    val question = Question(
                        id = questionResponse.id,
                        question = questionResponse.question,
                        response = questionResponse.response,
                        timestamp = timestamp,
                        hasAnswer = questionResponse.response != null
                    )

                    Result.Success(question)
                } else {
                    Result.Error(Exception("Error al obtener pregunta: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun addQuestion(question: String): Result<Question> =
        withContext(Dispatchers.IO) {
            try {
                val request = QuestionRequest(question)
                val response = apiService.addQuestion(request)

                if (response.isSuccessful && response.body() != null) {
                    val questionResponse = response.body()!!

                    // Convertir fecha de string a Date
                    val timestamp = try {
                        dateFormat.parse(questionResponse.createdAt) ?: Date()
                    } catch (e: Exception) {
                        Date()
                    }

                    val newQuestion = Question(
                        id = questionResponse.id,
                        question = questionResponse.question,
                        response = questionResponse.response,
                        timestamp = timestamp,
                        hasAnswer = questionResponse.response != null
                    )

                    Result.Success(newQuestion)
                } else {
                    Result.Error(Exception("Error al añadir pregunta: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun answerQuestion(questionId: Int, answer: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val request = AnswerRequest(
                    messageId = questionId,
                    response = answer
                )
                val response = apiService.answerQuestion(request)

                if (response.isSuccessful) {
                    Result.Success(true)
                } else {
                    Result.Error(Exception("Error al responder pregunta: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}