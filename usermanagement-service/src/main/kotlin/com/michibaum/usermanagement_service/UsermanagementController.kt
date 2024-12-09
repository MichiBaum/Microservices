package com.michibaum.usermanagement_service

import com.michibaum.usermanagement_library.LoginDto
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_library.UserManagementEndpoints
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UsermanagementController (
    private val userService: UserService
) : UserManagementEndpoints {

    override fun checkUserDetails(loginDto: LoginDto): UserDetailsDto? {
        val user = userService.findByUsername(loginDto.username) ?: return null
        val matching = userService.checkPassword(loginDto.password, user.password)
        if (!matching) return null

        return UserDetailsDto(
            id = user.id.toString(),
            username = user.username,
            permissions = user.permissions.map { it.permission }.toSet()
        )
    }

    @GetMapping("/api/user/{id}")
    fun get(@PathVariable id: String) =
        userService.getUser(id)
            .let { convertUserToDto(it) }
            .let { toResponseEntity(it) }

    @PostMapping("/api/user/{id}")
    fun update(@PathVariable id: String, @RequestBody updateUserDto: UpdateUserDto) =
        userService.update(id, updateUserDto)
            .let { toResponseEntity(it) }
}

fun convertUserToDto(user: User?) =
    user?.let {
        ReturnUserDto(user.id.toString(), user.username, user.email)
    }

fun <T> toResponseEntity(nullableEntity: T?): ResponseEntity<T> =
    nullableEntity?.let {
        ResponseEntity.ok(it)
    } ?: ResponseEntity.badRequest().build()
