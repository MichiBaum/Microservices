package com.michibaum.music_service.apis

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Represents the result of an API operation with type-safe success and error handling.
 *
 * @param T The type of data in case of success
 */
sealed class ApiResult<out T> {
    companion object {
        const val QUOTA_EXCEEDED_MESSAGE = "API quota limit has been exceeded"
        const val NOT_FOUND_MESSAGE = "Requested resource not found"
    }

    /**
     * Represents a successful API operation with returned data.
     *
     * @param data The successful result data
     * @param statusCode HTTP status code of the response
     */
    data class Success<T>(
        val data: T,
        val statusCode: Int = 200
    ) : ApiResult<T>()

    /**
     * Represents an API error with detailed information.
     *
     * @param message Detailed error message
     * @param statusCode HTTP status code of the error
     * @param cause The underlying cause of the error
     */
    data class ApiError(
        val message: String,
        val statusCode: Int,
        val cause: Throwable
    ) : ApiResult<Nothing>(), Loggable {
        override fun log() {
            logger.error("API Error [$statusCode]: $message", cause)
        }
    }

    /**
     * Represents a quota limit exceeded error.
     */
    data object QuotaLimitExceeded : ApiResult<Nothing>() {
        val message: String = QUOTA_EXCEEDED_MESSAGE
        val statusCode: Int = 429
    }

    /**
     * Represents a resource not found error.
     */
    data object NotFound : ApiResult<Nothing>() {
        val message: String = NOT_FOUND_MESSAGE
        val statusCode: Int = 404
    }
}

private interface Loggable {
    val logger: Logger
        get() = LoggerFactory.getLogger(this::class.java)

    fun log()
}