package com.michibaum.lifemanagementbackend.core.exceptionHandler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.util.*
import javax.persistence.EntityNotFoundException

@RestControllerAdvice
class CustomExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @Value("\${frontend.exception.shown}")
    private val exceptionShown: Boolean = false

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException, request: WebRequest): ResponseEntity<ErrorDetails> {
        logger.error(ex.message, ex.stackTrace)
        val errorDetails = ErrorDetails(
            timestamp = Date().time,
            message = ex.message ?: "",
            exceptionClass = ex.javaClass,
            details = request.getDescription(true)
        )
        return returnResponseEntry(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, request: WebRequest): ResponseEntity<ErrorDetails> {
        logger.error(ex.message, ex.stackTrace)
        val errorDetails = ErrorDetails(
            timestamp = Date().time,
            message = ex.message ?: "",
            exceptionClass = ex.javaClass,
            details = request.getDescription(true)
        )
        return returnResponseEntry(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ValidationErrorDetails> {
        logger.error(ex.message, ex.stackTrace)
        val validationErrors = ex.bindingResult.allErrors
            .map { obj: ObjectError -> obj.defaultMessage }
            .filter { obj: String? -> Objects.nonNull(obj) }

        if(exceptionShown) {
            val errorDetails = ValidationErrorDetails(
                timestamp = Date().time,
                message = ex.message,
                validationErrors = validationErrors,
                exceptionClass = ex.javaClass,
                details = request.getDescription(true)
            )
            return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
        }else{
            val errorDetails = ValidationErrorDetails(
                timestamp = Date().time,
                message = "",
                validationErrors = validationErrors,
                exceptionClass = ex.javaClass,
                details = request.getDescription(false)
            )
            return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
        }
    }

    private fun <T : ErrorDetails> returnResponseEntry(errorDetails: T, httpStatus: HttpStatus): ResponseEntity<T> {
        return if(exceptionShown){
            ResponseEntity(errorDetails, httpStatus)
        }else{
            ResponseEntity(httpStatus)
        }
    }
}

open class ErrorDetails(
    open val timestamp: Long,
    open val message: String,
    open val exceptionClass: Class<out Exception?>?,
    open val details: String
)

class ValidationErrorDetails(
    override val timestamp: Long,
    override val message: String,
    val validationErrors: List<String?>,
    override val exceptionClass: Class<out Exception?>?,
    override val details: String
) : ErrorDetails(timestamp, message, exceptionClass, details)
