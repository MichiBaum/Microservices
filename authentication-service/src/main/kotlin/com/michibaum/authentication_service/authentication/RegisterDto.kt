package com.michibaum.authentication_service.authentication

data class RegisterDto(
    val username: String,
    val email: String,
    val password: String
)
