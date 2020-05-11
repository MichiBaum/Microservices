package com.michibaum.lifemanagementbackend.security

import com.michibaum.lifemanagementbackend.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class LastLoginUpdater(
    private val userRepository: UserRepository
) {

    fun update() =
        SecurityContextHolder.getContext().authentication?.name?.let { username ->
            userRepository.findByName(username)?.let { user ->
                user.lastLogin = Date().time
                userRepository.save(user)
            }
        }

}
