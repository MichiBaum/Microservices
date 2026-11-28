package com.michibaum.usermanagement_service.app

import com.michibaum.usermanagement_library.*
import com.michibaum.usermanagement_service.database.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class UsermanagementController (
    private val userService: UserService
) : UserManagementEndpoints {

    override fun create(createUserDto: CreateUserDto): UserDetailsDto? {
        val errors = CreateUserDtoValidator.validate(createUserDto)
        if(errors.isNotEmpty())
            return null

        val alreadyExists = userService.emailExists(createUserDto.email)
        if (alreadyExists)
            return null

        val user = userService.createDefaultUser(createUserDto)
        val userPermissionMapping = userService.addDefaultPermissions(user)
        val dto = UserDetailsDto(
            id = user.id.toString(),
            permissions = userPermissionMapping.map { it.permission.permission }.toSet()
        )
        return dto
    }

    override fun getUserDetailsById(id: String): UserDetailsDto? {
        val userId = try {
            UUID.fromString(id)
        } catch (_: Exception) {
            return null
        }
        return userService.getUserDetailsById(userId)
    }

    override fun delete(id: String): ResponseEntity<Void> {
        val userId = try {
            UUID.fromString(id)
        } catch (_: Exception) {
            return ResponseEntity.badRequest().build()
        }
        userService.deleteUser(userId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/api/user/{id}")
    fun get(@PathVariable id: String): ResponseEntity<ReturnUserDto>{
        val userId = UUID.fromString(id)
        return userService.getUser(userId)
            .let { convertUserToDto(it) }
            .let { toResponseEntity(it) }
    }

    @PostMapping("/api/user/{id}")
    fun update(@PathVariable id: String, @RequestBody updateUserDto: UpdateUserDto): ResponseEntity<ReturnUserDto>{
        val userId = UUID.fromString(id)
        return userService.update(userId, updateUserDto)
            .let { convertUserToDto(it) }
            .let { toResponseEntity(it) }
    }

    fun convertUserToDto(user: User?): ReturnUserDto? =
        user?.let {
            ReturnUserDto(user.id.toString(), user.email)
        }

    fun <T : Any> toResponseEntity(nullableEntity: T?): ResponseEntity<T> =
        nullableEntity?.let { entity ->
            ResponseEntity.ok(entity)
        } ?: ResponseEntity.badRequest().build()
}

