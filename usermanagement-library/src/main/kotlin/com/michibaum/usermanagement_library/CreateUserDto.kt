package com.michibaum.usermanagement_library

data class CreateUserDto(
    val username: String,
    val email: String,
    val password: String
)
