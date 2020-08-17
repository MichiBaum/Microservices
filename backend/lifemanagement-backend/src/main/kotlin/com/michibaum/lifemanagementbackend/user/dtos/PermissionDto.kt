package com.michibaum.lifemanagementbackend.user.dtos

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Return Permission DTO")
data class ReturnPermissionDto(

    @field:Schema(
        example = "SEE_LOGS",
        required = true
    )
    val name: String,

    @field:Schema(
        example = "12",
        required = true
    )
    val id: Long
)

@Schema(name = "Return Permission DTO")
data class UpdatePermissionDto(

    @field:Schema(
        example = "SEE_LOGS",
        required = true
    )
    val name: String,

    @field:Schema(
        example = "12",
        required = true
    )
    val id: Long
)
