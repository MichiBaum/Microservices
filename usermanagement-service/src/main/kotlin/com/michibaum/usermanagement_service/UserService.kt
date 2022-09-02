package com.michibaum.usermanagement_service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun getUser(id: String) =
        userRepository.findById(id)
            .convertToNullable()

    fun update(id: String, updateUserDto: UpdateUserDto): User? =
        userRepository.findById(id)
            .convertToNullable()
            ?.copy(
                username = updateUserDto.username,
                email = updateUserDto.email,
                password = encodePassword(updateUserDto.password)
            )
            ?.let { userRepository.save(it) }

    fun encodePassword(password: String): String =
        passwordEncoder.encode(password)

}