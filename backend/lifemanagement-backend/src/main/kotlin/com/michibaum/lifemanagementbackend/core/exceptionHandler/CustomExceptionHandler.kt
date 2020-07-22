package com.michibaum.lifemanagementbackend.core.exceptionHandler

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.validation.ConstraintViolationException

@ControllerAdvice
class CustomExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @Value("\${frontend.exception.shown}")
    private val exceptionShown: Boolean = false

    open class ErrorDetails(
        private val timestamp: Long,
        private val message: String,
        private val exceptionClass: Class<out Exception?>,
        private val details: String
    )

    data class ValidationErrorDetails(
        private val timestamp: Long,
        private val message: String,
        private val validationErrors: List<String>,
        private val exceptionClass: Class<out Exception?>,
        private val details: String
    ) : ErrorDetails(timestamp, message, exceptionClass, details)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException, request: WebRequest): ResponseEntity<Any> {
        logger.error(ex.message, ex.stackTrace)
        val errorDetails = ErrorDetails(Date().time, ex.message ?: "", ex.javaClass, request.getDescription(false))
        return returnResponseEntry(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        logger.error(ex.message, ex.stackTrace)
        val errorDetails = ErrorDetails(Date().time, ex.message ?: "", ex.javaClass, request.getDescription(false))
        return returnResponseEntry(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<Any> {
        logger.error(ex.message, ex.stackTrace)
        val validationErrors = ex.bindingResult.allErrors
            .map { obj: ObjectError -> obj.code }
            .filter { obj: String? -> Objects.nonNull(obj) }
        val errorDetails = ValidationErrorDetails(Date().time, ex.message ?: "", validationErrors, ex.javaClass, request.getDescription(false))
        return returnResponseEntry(errorDetails, HttpStatus.BAD_REQUEST)
    }

    private fun returnResponseEntry(errorDetails: ErrorDetails, httpStatus: HttpStatus): ResponseEntity<Any> {
        return if(exceptionShown){
            ResponseEntity(errorDetails, httpStatus)
        }else{
            ResponseEntity(httpStatus)
        }
    }
}
