package com.michibaum.usermanagement_service

data class ReturnUserDto(
    val id: String,
    val username: String,
    val email: String,
)

data class UpdateUserDto(
    val username: String,
    val email: String,
    val password: String
)