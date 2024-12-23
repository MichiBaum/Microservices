package com.michibaum.authentication_service.authentication

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RegisterDto(
    @NotBlank
    val username: String,
    @NotBlank
    @Email
    val email: String,
    @NotBlank
    val password: String
)
