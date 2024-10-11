package com.michibaum.usermanagement_service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun getUser(id: String) = findUserById(id)

    fun update(id: String, updateUserDto: UpdateUserDto): User? {
        val foundUser = userRepository.findById(id).orElse(null)
        return foundUser?.let {
            // TODO update User
            userRepository.save(foundUser)
        }
    }

    private fun findUserById(id: String) = userRepository.findById(id).orElseNull()

}

private fun <T> Optional<T>.orElseNull(): T? = this.orElse(null)