package com.michibaum.lifemanagementbackend.user.service

import com.michibaum.lifemanagementbackend.permission.domain.Permission
import com.michibaum.lifemanagementbackend.permission.service.PermissionService
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.dtos.UpdateUserDto
import com.michibaum.lifemanagementbackend.user.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    userRepository: UserRepository,
    permissionService: PermissionService,
    bCryptPasswordEncoder: BCryptPasswordEncoder
) {

    val findByName: (name: String) -> User? =
        userRepository::findByName

    val save: (User) -> User =
        userRepository::saveAndFlush

    val findAll: () -> List<User> =
        userRepository::findAll

    val updateUser = fun(user: User, userDto: UpdateUserDto) =
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
                false -> bCryptPasswordEncoder.encode(userDto.password)
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
