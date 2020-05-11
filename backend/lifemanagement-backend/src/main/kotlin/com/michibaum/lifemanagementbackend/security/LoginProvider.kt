package com.michibaum.lifemanagementbackend.security

import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class LoginProvider(
    private val userRepository: UserRepository
) {

    fun currentUser(): User? {
        val username = SecurityContextHolder.getContext().authentication?.name ?: return null
        return userRepository.findByName(username)
    }
}
