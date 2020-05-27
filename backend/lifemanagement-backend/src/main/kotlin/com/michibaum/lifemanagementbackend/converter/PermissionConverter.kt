package com.michibaum.lifemanagementbackend.converter

import com.michibaum.lifemanagementbackend.domain.Permission
import com.michibaum.lifemanagementbackend.dtos.ReturnPermissionDto

fun Permission.toDto() =
    ReturnPermissionDto(
        name = name.toString(),
        id = id
    )
