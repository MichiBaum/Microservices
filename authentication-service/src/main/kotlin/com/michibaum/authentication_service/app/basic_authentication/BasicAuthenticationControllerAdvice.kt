package com.michibaum.authentication_service.app.basic_authentication

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [ChangePasswordController::class])
class BasicAuthenticationControllerAdvice {

    @ExceptionHandler(InvalidCurrentPasswordException::class)
    fun handleInvalidCurrentPassword(ex: InvalidCurrentPasswordException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.message ?: "Current password does not match"
        ).apply {
            title = "INVALID_CURRENT_PASSWORD"
            setProperty("code", "INVALID_CURRENT_PASSWORD")
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }

    @ExceptionHandler(SamePasswordException::class)
    fun handleSamePassword(ex: SamePasswordException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.message ?: "New password must be different from current password"
        ).apply {
            title = "SAME_PASSWORD"
            setProperty("code", "SAME_PASSWORD")
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }

    @ExceptionHandler(UserCredentialsNotFoundException::class)
    fun handleUserCredentialsNotFound(ex: UserCredentialsNotFoundException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.message ?: "User credentials not found"
        ).apply {
            title = "NO_PASSWORD_CREDENTIALS"
            setProperty("code", "NO_PASSWORD_CREDENTIALS")
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ProblemDetail> {
        val errors = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            errors.ifBlank { "Validation failed" }
        ).apply {
            title = "VALIDATION_FAILED"
            setProperty("code", "VALIDATION_FAILED")
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail)
    }
}
