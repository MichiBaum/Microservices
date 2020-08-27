package com.michibaum.lifemanagementbackend.core.security

import com.michibaum.lifemanagementbackend.core.date.RealDate
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class LastLoginUpdater(
    private val userRepository: UserRepository
) {

    fun update(
        username: String? = SecurityContextHolder.getContext().authentication?.name
    ): User? {
        username?.let { name ->
            userRepository.findByName(name)?.let { user ->
                user.lastLogin = RealDate().millisecconds
                return userRepository.save(user)
            }
        }
        return null
    }
}
