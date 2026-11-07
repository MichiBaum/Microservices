package com.michibaum.usermanagement_library

data class UserDetailsDto(
    val id: String,
    val username: String? = null,
    val permissions: Set<String>
)
