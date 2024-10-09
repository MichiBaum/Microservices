package com.michibaum.chess.apis

import org.slf4j.LoggerFactory


/**
 * A sealed class representing the result of an API call.
 *
 * This class can encapsulate either a successful result, an error message, or an exception thrown during the API call.
 *
 * @param T The type of the result contained within this class.
 */
sealed class ApiResult<T>

/**
 * Represents a successful result of an API call within the [ApiResult] sealed class.
 *
 * The [Success] class wraps a successful outcome, containing the result of type [T].
 *
 * @param T The type of the result contained within this success instance.
 * @property result The result of the API call.
 */
data class Success<T>(val result: T) : ApiResult<T>()

/**
 * Represents an entity capable of logging information.
 *
 * The `Loggable` interface is intended to be implemented by classes that need to perform logging of important actions,
 * errors, or events. The implementing classes are required to provide their own implementation of the `log` method.
 */
interface Loggable{
    fun log()
}

/**
 * Represents an error result of an API call, encapsulating the error message and providing logging capabilities.
 *
 * @param T The type of the result which this error result is associated with.
 * @property error The error message detailing what went wrong during the API call.
 */
data class Error<T>(val error: String): ApiResult<T>(), Loggable{
    private val logger = LoggerFactory.getLogger(Error::class.java)
    override fun log(){
        logger.error(error)
    }
}

/**
 * Represents an exception result of an API call.
 *
 * The `Exception` class extends `ApiResult` and `Loggable`, capturing exception details and providing logging capabilities.
 *
 * @param T The type of the result that would have been returned if the call had succeeded.
 * @property message The error message describing the exception.
 * @property throwable The actual exception thrown during the API call.
 */
data class Exception<T>(val message: String, val throwable: Throwable): ApiResult<T>(), Loggable{
    private val logger = LoggerFactory.getLogger(Exception::class.java)
    override fun log(){
        logger.error(message + " " + throwable.message, throwable)
    }
}