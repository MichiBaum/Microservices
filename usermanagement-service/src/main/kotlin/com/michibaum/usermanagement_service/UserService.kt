package com.michibaum.usermanagement_service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun getUser(id: String) = userRepository.findById(id).orElseNull()

    fun update(id: String, updateUserDto: UpdateUserDto): User? {
        val foundUser = userRepository.findById(id).orElseNull()
        return foundUser?.let {
            // TODO update User
            userRepository.save(foundUser)
        }
    }

    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun checkPassword(dtoPassword: String, passwordHash: String): Boolean {
        return passwordEncoder.matches(dtoPassword, passwordHash)
    }

}

private fun <T> Optional<T>.orElseNull(): T? = this.orElse(null)