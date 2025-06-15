package com.michibaum.usermanagement_service.app

import com.michibaum.permission_library.Permissions
import com.michibaum.usermanagement_library.CreateUserDto
import com.michibaum.usermanagement_service.database.PermissionRepository
import com.michibaum.usermanagement_service.database.User
import com.michibaum.usermanagement_service.database.UserPermissionMapping
import com.michibaum.usermanagement_service.database.UserPermissionMappingRepository
import com.michibaum.usermanagement_service.database.UserRepository
import io.micrometer.observation.annotation.Observed
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val permissionRepository: PermissionRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userPermissionMappingRepository: UserPermissionMappingRepository
) {

    fun getUser(id: UUID) = userRepository.findById(id).orElseNull()

    fun update(id: UUID, updateUserDto: UpdateUserDto): User? {
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
        val user = User(
            username = createUserDto.username,
            email = createUserDto.email,
            password = passwordEncoder.encode(createUserDto.password),
        )
        return userRepository.save(user)
    }

    fun addDefaultPermissions(user: User): List<UserPermissionMapping> {
        val defaultPermissions = listOf(
            Permissions.USERMANAGEMENT_SERVICE_EDIT_OWN_USER.name,
            Permissions.CHESS_SERVICE.name
        )
        val permissions = permissionRepository.findAllById(defaultPermissions)
        val x = permissions.map { UserPermissionMapping(user, it) }
        return userPermissionMappingRepository.saveAll(x)
    }

}

private fun <T> Optional<T>.orElseNull(): T? = this.orElse(null)