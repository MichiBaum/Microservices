package com.michibaum.usermanagement_service.app

import com.michibaum.usermanagement_service.database.User
import org.springframework.stereotype.Component

@Component
class UserConverter {

    fun toDto(user: User?): ReturnUserDto? =
        user?.let {
            ReturnUserDto(user.id.toString(), user.username, user.email)
        }

    fun toDto(user: User): ReturnUserDto =
        ReturnUserDto(user.id.toString(), user.username, user.email)
}