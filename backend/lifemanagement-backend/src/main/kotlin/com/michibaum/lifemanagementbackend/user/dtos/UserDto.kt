package com.michibaum.lifemanagementbackend.user.dtos

import com.michibaum.lifemanagementbackend.permission.dtos.ReturnPermissionDto

data class ReturnUserDto(
    val id: Long,
    val name: String,
    val emailAddress: String,
    val enabled: Boolean,
    val lastLogin: Long,
    val permissions: List<ReturnPermissionDto> = mutableListOf()
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
