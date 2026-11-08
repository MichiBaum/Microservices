package com.michibaum.usermanagement_service.app

import com.michibaum.permission_library.Permissions
import com.michibaum.usermanagement_library.CreateUserDto
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_service.database.PermissionRepository
import com.michibaum.usermanagement_service.database.User
import com.michibaum.usermanagement_service.database.UserPermissionMapping
import com.michibaum.usermanagement_service.database.UserPermissionMappingRepository
import com.michibaum.usermanagement_service.database.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val permissionRepository: PermissionRepository,
    private val userPermissionMappingRepository: UserPermissionMappingRepository
) {

    fun getUser(id: UUID) = userRepository.findById(id).orElseNull()

    fun getUserDetailsById(id: UUID): UserDetailsDto? {
        val user = userRepository.findById(id).orElseNull() ?: return null
        val permissions = permissionRepository.findByUser(user)
        return UserDetailsDto(
            id = user.id.toString(),
            permissions = permissions.map { it.permission }.toSet()
        )
    }

    @Transactional
    fun deleteUser(id: UUID) {
        val user = userRepository.findById(id).orElseNull() ?: return
        userPermissionMappingRepository.deleteByUser(user)
        userRepository.delete(user)
    }

    fun update(id: UUID, updateUserDto: UpdateUserDto): User? {
        val foundUser = userRepository.findById(id).orElseNull()
        return foundUser?.let {
            // TODO update User
            userRepository.save(foundUser)
        }
    }

    fun emailExists(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    fun createDefaultUser(createUserDto: CreateUserDto): User {
        val user = User(
            email = createUserDto.email
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