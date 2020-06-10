package com.michibaum.lifemanagementbackend.dtos

data class ReturnPermissionDto(
    val name: String,
    val id: Long
)

data class UpdatePermissionDto(
    val name: String,
    val id: Long
)
