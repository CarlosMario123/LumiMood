package com.example.psicologic.core.domain.models

/**
 * Clase genérica que representa el resultado de una operación que puede tener éxito o fallar.
 * Utilizada principalmente en operaciones de repositorio y casos de uso.
 */
sealed class Result<out T> {
    /**
     * Representa una operación exitosa con datos resultantes
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Representa una operación fallida con un error
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Representa una operación en curso
     */
    object Loading : Result<Nothing>()

    /**
     * Funciones de extensión para facilitar el manejo de los resultados
     */
    companion object {
        /**
         * Crea un Result.Success con los datos proporcionados
         */
        fun <T> success(data: T): Result<T> = Success(data)

        /**
         * Crea un Result.Error con la excepción proporcionada
         */
        fun error(exception: Exception): Result<Nothing> = Error(exception)

        /**
         * Crea un Result.Error con un mensaje de error
         */
        fun error(message: String): Result<Nothing> = Error(Exception(message))

        /**
         * Representa un estado de carga
         */
        fun loading(): Result<Nothing> = Loading
    }
}

/**
 * Ejecuta el bloque proporcionado y captura cualquier excepción, devolviendo un Result adecuado
 */
inline fun <T> runCatching(block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e)
    }
}