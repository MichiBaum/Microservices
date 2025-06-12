package com.michibaum.usermanagement_service.app

import com.michibaum.usermanagement_library.*
import com.michibaum.usermanagement_service.database.PermissionRepository
import com.michibaum.usermanagement_service.database.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class UsermanagementController (
    private val userService: UserService,
    private val permiRepository: PermissionRepository
) : UserManagementEndpoints {

    override fun checkUserDetails(loginDto: LoginDto): UserDetailsDto? {
        val errors = LoginDtoValidator.validate(loginDto)
        if(errors.isNotEmpty())
            return null

        val user = userService.findByUsername(loginDto.username)
        if (user == null) {
            // Prevent timing to check user exists
            var sleepTime = (150L..250L).random()
            Thread.sleep(sleepTime)
            return null
        }
        val matching = userService.checkPassword(loginDto.password, user.password)
        if (!matching)
            return null
        
        val permissions = permiRepository.findByUser(user)
        return UserDetailsDto(
            id = user.id.toString(),
            username = user.username,
            permissions = permissions.map { it.permission }.toSet()
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
        val userPermissionMapping = userService.addDefaultPermissions(user)
        val dto = UserDetailsDto(
            id = user.id.toString(),
            username = user.username,
            permissions = userPermissionMapping.map { it.permission.permission }.toSet()
        )
        return dto
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
            ReturnUserDto(user.id.toString(), user.username, user.email)
        }

    fun <T> toResponseEntity(nullableEntity: T?): ResponseEntity<T> =
        nullableEntity?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.badRequest().build()
}

