package com.michibaum.lifemanagementbackend.user.dtos

data class ReturnUserDto(
    val id: Long,
    val name: String,
    val emailAddress: String,
    val enabled: Boolean,
    val lastLogin: Long,
    val permissions: List<ReturnPermissionDto>
)

data class UpdateUserDto(
    val id: Long = 0,
    val name: String,
    val password: String,
    val emailAddress: String,
    val enabled: Boolean,
    val lastLogin: Long,
    val permissions: List<Long> = mutableListOf()
)
