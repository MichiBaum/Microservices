package com.michibaum.lifemanagementbackend.user.service

import com.michibaum.lifemanagementbackend.user.domain.Permission
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.dtos.UpdateUserDto
import com.michibaum.lifemanagementbackend.user.repository.UserRepository
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val permissionService: PermissionService,
    private val argon2PasswordEncoder: Argon2PasswordEncoder
) {

    fun findByName(name: String): User? =
        userRepository.findByName(name)

    fun save(user: User): User =
        userRepository.save(user)

    fun findAll(): List<User> =
        userRepository.findAll()

    fun changePermissions(user: User, permissionIds: List<Long>): User =
        user.apply {
            permissions = permissionIds
                .mapNotNull(permissionService::findById) as MutableList<Permission>
        }


    val update = fun(user: User, userDto: UpdateUserDto) =
        user.apply {
            name = when (userDto.name.isBlank()) {
                true -> name
                false -> userDto.name
            }
            emailAddress = when (userDto.emailAddress.isBlank()) {
                true -> emailAddress
                false -> userDto.emailAddress
            }
            password = when (userDto.password.isBlank()) {
                true -> password
                false -> argon2PasswordEncoder.encode(userDto.password)
            }
            enabled = userDto.enabled
            permissions = when (userDto.permissions.isEmpty()) {
                true -> permissions
                false -> {
                    userDto.permissions.mapNotNull(permissionService::findById) as MutableList<Permission>
                }
            }
        }
}
