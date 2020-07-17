package com.michibaum.lifemanagementbackend.user.converter

import com.michibaum.lifemanagementbackend.user.domain.Permission
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.dtos.ReturnUserDto

fun User.toDto(): ReturnUserDto =
    ReturnUserDto(
        id = id,
        permissions = permissions.map(Permission::toDto),
        enabled = enabled,
        emailAddress = emailAddress,
        name = name,
        lastLogin = lastLogin
    )
