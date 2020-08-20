package com.michibaum.lifemanagementbackend.core.exceptionHandler

import io.swagger.v3.oas.annotations.media.Schema
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
            message = if(exceptionShown) ex.message ?: "" else "",
            exceptionClass = if(exceptionShown) ex.javaClass else null,
            details = if(exceptionShown) request.getDescription(true) else request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, request: WebRequest): ResponseEntity<ErrorDetails> {
        logger.error(ex.message, ex.stackTrace)
        val errorDetails = ErrorDetails(
            timestamp = Date().time,
            message = if(exceptionShown) ex.message ?: "" else "",
            exceptionClass = if(exceptionShown) ex.javaClass else null,
            details = if(exceptionShown) request.getDescription(true) else request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ValidationErrorDetails> {
        logger.error(ex.message, ex.stackTrace)
        val validationErrors = ex.bindingResult.allErrors
            .map { obj: ObjectError -> obj.defaultMessage }
            .filter { obj: String? -> Objects.nonNull(obj) }

        val errorDetails = ValidationErrorDetails(
            timestamp = Date().time,
            message = if(exceptionShown) ex.message else "",
            validationErrors = validationErrors,
            exceptionClass = if(exceptionShown) ex.javaClass else null,
            details = if(exceptionShown) request.getDescription(true) else request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

}
