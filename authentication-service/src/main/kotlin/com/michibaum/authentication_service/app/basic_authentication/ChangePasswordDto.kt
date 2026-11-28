package com.michibaum.authentication_service.app.basic_authentication

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChangePasswordDto(
    @field:NotBlank(message = "Current password must not be blank")
    val currentPassword: String,

    @field:NotBlank(message = "New password must not be blank")
    @field:Size(min = 8, message = "New password must be at least 8 characters long")
    val newPassword: String
)
