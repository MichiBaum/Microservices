package com.michibaum.authentication_service.app.authentication

data class RegisterResponse(
    val state: RegisterState,
    val username: String,
    val email: String
)

enum class RegisterState {
    SUCCESS,
    ERROR
}