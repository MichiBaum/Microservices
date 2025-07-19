package com.michibaum.authentication_service.app.authentication

data class RegisterDto(
    val username: String,
    val email: String,
    val password: String
)
