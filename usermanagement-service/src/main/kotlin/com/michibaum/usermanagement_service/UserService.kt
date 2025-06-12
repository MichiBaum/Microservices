package com.michibaum.usermanagement_service

import com.michibaum.permission_library.Permissions
import com.michibaum.usermanagement_library.CreateUserDto
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.annotation.Observed
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val permissionRepository: PermissionRepository,
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

    @Observed(name = "password-check")
    fun checkPassword(dtoPassword: String, passwordHash: String): Boolean {
        return passwordEncoder.matches(dtoPassword, passwordHash)
    }

    fun anyExists(username: String, email: String): Boolean {
        return userRepository.existsByUsernameOrEmail(username, email)
    }

    fun createDefaultUser(createUserDto: CreateUserDto): User {
        val permissions = permissionRepository.findById(Permissions.USERMANAGEMENT_SERVICE_EDIT_OWN_USER.name).orElseThrow()
        val user = User(
            username = createUserDto.username,
            email = createUserDto.email,
            password = passwordEncoder.encode(createUserDto.password),
            permissions = setOf(permissions),
        )
        return userRepository.save(user)
    }

}

private fun <T> Optional<T>.orElseNull(): T? = this.orElse(null)