package com.michibaum.usermanagement_service

import com.michibaum.usermanagement_library.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UsermanagementController (
    private val userService: UserService
) : UserManagementEndpoints {

    override fun checkUserDetails(loginDto: LoginDto): UserDetailsDto? {
        val errors = LoginDtoValidator.validate(loginDto)
        if(errors.isNotEmpty())
            return null

        val user = userService.findByUsername(loginDto.username) ?: return null
        val matching = userService.checkPassword(loginDto.password, user.password)
        if (!matching) return null

        return UserDetailsDto(
            id = user.id.toString(),
            username = user.username,
            permissions = user.permissions.map { it.permission }.toSet()
        )
    }

    override fun create(createUserDto: CreateUserDto): UserDetailsDto? {
        val errors = CreateUserDtoValidator.validate(createUserDto)
        if(errors.isNotEmpty())
            return null

        val alreadyExists = userService.anyExists(createUserDto.username, createUserDto.email)
        if (alreadyExists)
            return null

        val user = userService.createDefaultUser(createUserDto)
        val dto = UserDetailsDto(
            id = user.id.toString(),
            username = user.username,
            permissions = user.permissions.map { it.permission }.toSet()
        )
        return dto
    }

    @GetMapping("/api/user/{id}")
    fun get(@PathVariable id: String): ResponseEntity<ReturnUserDto> =
        userService.getUser(id)
            .let { convertUserToDto(it) }
            .let { toResponseEntity(it) }

    @PostMapping("/api/user/{id}")
    fun update(@PathVariable id: String, @RequestBody updateUserDto: UpdateUserDto): ResponseEntity<ReturnUserDto> =
        userService.update(id, updateUserDto)
            .let { convertUserToDto(it) }
            .let { toResponseEntity(it) }

    fun convertUserToDto(user: User?): ReturnUserDto? =
        user?.let {
            ReturnUserDto(user.id.toString(), user.username, user.email)
        }

    fun <T> toResponseEntity(nullableEntity: T?): ResponseEntity<T> =
        nullableEntity?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.badRequest().build()
}

