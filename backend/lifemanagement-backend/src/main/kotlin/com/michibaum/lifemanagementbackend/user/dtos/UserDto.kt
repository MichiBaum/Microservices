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
    @NotNull(message = "user.validation.id.notNull")
    val id: Long = 0,

    @NotBlank(message = "user.validation.name.notEmpty")
    @NotNull(message = "user.validation.name.notNull")
    val name: String,

    @NotNull(message = "user.validation.password.notNull")
    val password: String,

    @Email(message = "user.validation.emailAddress.notValid")
    @NotBlank(message = "user.validation.emailAddress.notEmpty")
    @NotNull(message = "user.validation.emailAddress.notNull")
    val emailAddress: String,

    @NotNull(message = "user.validation.enabled.notNull")
    val enabled: Boolean,

    @NotNull(message = "user.validation.lastLogin.notNull")
    val lastLogin: Long,

    @NotNull(message = "user.validation.permissions.notNull")
    val permissions: List<Long> = mutableListOf()
)
