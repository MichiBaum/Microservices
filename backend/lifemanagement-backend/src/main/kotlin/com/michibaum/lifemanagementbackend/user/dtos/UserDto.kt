package com.michibaum.lifemanagementbackend.user.dtos

import io.swagger.v3.oas.annotations.media.Schema
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
    @field:Schema(example = "12", required = false)
    @field:NotNull(message = "validation.user.id.notNull")
    val id: Long = 0,

    @field:Schema(example = "Max Muster", required = false)
    @field:NotBlank(message = "validation.user.name.notEmpty")
    val name: String,

    @field:Schema(example = "plsDoNotUseThisAsPassword", required = false)
    @field:NotNull(message = "validation.user.password.notNull")
    val password: String,

    @field:Schema(example = "max.msuter@jahuu.com", required = false)
    @field:Email(message = "validation.user.emailAddress.notValid")
    @field:NotBlank(message = "validation.user.emailAddress.notEmpty")
    val emailAddress: String,

    @field:Schema(example = "true", required = true)
    @field:NotNull(message = "validation.user.enabled.notNull")
    val enabled: Boolean,

    @field:Schema(example = "1595851650180", required = false)
    @field:NotNull(message = "validation.user.lastLogin.notNull")
    val lastLogin: Long,

    @field:Schema(example = "{2,6,34}", required = false, description = "List of id from the permissions")
    @field:NotNull(message = "validation.user.permissions.notNull")
    val permissions: List<Long> = mutableListOf()
)
