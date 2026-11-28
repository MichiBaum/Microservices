package com.michibaum.usermanagement_service.app

data class ReturnUserDto(
    val id: String,
    val email: String,
)

data class UpdateUserDto(
    val email: String
)