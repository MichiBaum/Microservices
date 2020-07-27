package com.michibaum.lifemanagementbackend.user.dtos

import io.swagger.v3.oas.annotations.media.Schema

data class ReturnPermissionDto(

    @field:Schema(example = "SEE_LOGS", required = true)
    val name: String,

    @field:Schema(example = "12", required = true)
    val id: Long
)

data class UpdatePermissionDto(

    @field:Schema(example = "SEE_LOGS", required = true)
    val name: String,

    @field:Schema(example = "12", required = true)
    val id: Long
)
