package com.michibaum.lifemanagementbackend.converter

import com.michibaum.lifemanagementbackend.domain.Permission
import com.michibaum.lifemanagementbackend.domain.User
import com.michibaum.lifemanagementbackend.dtos.ReturnUserDto

fun User.toDto(): ReturnUserDto =
    ReturnUserDto(
        id = id,
        permissions = permissions.map(Permission::toDto),
        enabled = enabled,
        emailAddress = emailAddress,
        name = name,
        lastLogin = lastLogin
    )
