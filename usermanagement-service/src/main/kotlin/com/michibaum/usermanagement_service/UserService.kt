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

    fun update(id: String, updateUserDto: UpdateUserDto): User? =
        this.findUserById(id)
            ?.copy(
                username = updateUserDto.username,
                email = updateUserDto.email,
                password = passwordEncoder.encode(updateUserDto.password)
            )
            ?.let { userRepository.save(it) }

    private fun findUserById(id: String) = userRepository.findById(id).orElseNull()

}

private fun <T> Optional<T>.orElseNull(): T? = this.orElse(null)