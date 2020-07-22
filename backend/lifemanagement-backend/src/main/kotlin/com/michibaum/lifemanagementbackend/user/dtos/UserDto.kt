package com.michibaum.lifemanagementbackend.user.dtos

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ReturnUserDto(
    val id: Long,
    val name: String,
    val emailAddress: String,
    val enabled: Boolean,
    val lastLogin: Long,
    val permissions: List<ReturnPermissionDto>
)

data class UpdateUserDto(
    @field:NotNull(message = "validation.user.id.notNull")
    val id: Long = 0,

    @field:NotBlank(message = "validation.user.name.notEmpty")
    @field:NotNull(message = "validation.user.name.notNull")
    val name: String,

    @field:NotNull(message = "validation.user.password.notNull")
    val password: String,

    @field:Email(message = "validation.user.emailAddress.notValid")
    @field:NotBlank(message = "validation.user.emailAddress.notEmpty")
    @field:NotNull(message = "validation.user.emailAddress.notNull")
    val emailAddress: String,

    @field:NotNull(message = "validation.user.enabled.notNull")
    val enabled: Boolean,

    @field:NotNull(message = "validation.user.lastLogin.notNull")
    val lastLogin: Long,

    @field:NotNull(message = "validation.user.permissions.notNull")
    val permissions: List<Long> = mutableListOf()
)
