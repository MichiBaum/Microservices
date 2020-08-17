package com.michibaum.lifemanagementbackend.user.dtos

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Schema(name = "Return User DTO")
data class ReturnUserDto(

    @field:Schema(
        example = "12",
        required = true
    )
    val id: Long,

    @field:Schema(
        example = "Max Muster",
        required = true
    )
    val name: String,

    @field:Schema(
        example = "max.muster@jahuu.com",
        required = true
    )
    val emailAddress: String,

    @field:Schema(
        example = "true",
        required = true
    )
    val enabled: Boolean,

    @field:Schema(
        example = "23544563",
        required = true
    )
    val lastLogin: Long,

    @field:Schema(
        required = false,
        description = "List of permission dtos"
    )
    val permissions: List<ReturnPermissionDto>
)

@Schema(name = "Update User DTO")
data class UpdateUserDto(

    @field:Schema(
        example = "12",
        required = false,
        defaultValue = "0"
    )
    @field:NotNull(
        message = "validation.user.id.notNull"
    )
    val id: Long = 0,

    @field:Schema(
        example = "Max Muster",
        required = false
    )
    @field:NotBlank(message = "validation.user.name.notEmpty")
    val name: String,

    @field:Schema(
        example = "plsDoNotUseThisAsPassword",
        required = false
    )
    @field:NotNull(
        message = "validation.user.password.notNull"
    )
    val password: String,

    @field:Schema(
        example = "max.muster@jahuu.com",
        required = false
    )
    @field:Email(
        message = "validation.user.emailAddress.notValid"
    )
    @field:NotBlank(
        message = "validation.user.emailAddress.notEmpty"
    )
    val emailAddress: String,

    @field:Schema(
        example = "true",
        required = true
    )
    @field:NotNull(
        message = "validation.user.enabled.notNull"
    )
    val enabled: Boolean,

    @field:Schema(
        example = "{2,6,34}",
        required = false,
        description = "List of id from the permissions",
        defaultValue = "{}"
    )
    @field:NotNull(
        message = "validation.user.permissions.notNull"
    )
    val permissions: List<Long> = mutableListOf()
)
