package com.michibaum.usermanagement_library

data class UserDetailsDto(
    val id: String,
    val username: String,
    val permissions: Set<String>
)
