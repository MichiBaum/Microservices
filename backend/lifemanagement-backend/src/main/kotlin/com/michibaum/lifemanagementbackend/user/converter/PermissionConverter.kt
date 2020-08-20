package com.michibaum.lifemanagementbackend.user.converter

import com.michibaum.lifemanagementbackend.user.domain.Permission
import com.michibaum.lifemanagementbackend.user.dtos.ReturnPermissionDto

fun Permission.toDto() =
    ReturnPermissionDto(
        name = name.toString(),
        id = id
    )
