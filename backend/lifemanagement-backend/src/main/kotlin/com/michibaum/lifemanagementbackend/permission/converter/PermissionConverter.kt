package com.michibaum.lifemanagementbackend.permission.converter

import com.michibaum.lifemanagementbackend.permission.domain.Permission
import com.michibaum.lifemanagementbackend.permission.dtos.ReturnPermissionDto

fun Permission.toDto()  =
    ReturnPermissionDto(
        name = name.toString(),
        id = id
    )
