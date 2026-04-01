package com.michibaum.usermanagement_service.app

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.permission_library.Permissions
import com.michibaum.usermanagement_library.*
import com.michibaum.usermanagement_service.database.PermissionRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class UsermanagementController (
    private val userService: UserService,
    private val permissionRepository: PermissionRepository,
    private val userConverter: UserConverter
) : UserManagementEndpoints {

    override fun checkUserDetails(loginDto: LoginDto): UserDetailsDto? {
        val errors = LoginDtoValidator.validate(loginDto)
        if(errors.isNotEmpty())
            return null

        val user = userService.findByUsername(loginDto.username)
        if (user == null) {
            // Prevent timing to check user exists
            val sleepTime = (150L..250L).random()
            Thread.sleep(sleepTime)
            return null
        }
        val matching = userService.checkPassword(loginDto.password, user.password)
        if (!matching)
            return null
        
        val permissions = permissionRepository.findByUser(user)
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

    @GetMapping("/api/users/me")
    fun get(principal: JwtAuthentication): ResponseEntity<ReturnUserDto>{
        val userId = principal.getUserUuid() ?: return ResponseEntity.badRequest().build()
        return userService.getUser(userId)
            .let { userConverter.toDto(it) }
            .let { toResponseEntity(it) }
    }

    @GetMapping("/api/users")
    fun getAll(principal: JwtAuthentication): ResponseEntity<List<ReturnUserDto>>{
        val hasAuthorityEditAll = principal.authorities.map { it.authority }.contains(Permissions.SERMANAGEMENT_SERVICE_EDIT_ALL_USER.name)
        if(!hasAuthorityEditAll)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        
        return userService.getAllUsers()
            .map { userConverter.toDto(it) }
            .let { toResponseEntity(it) }
        
    }

    @PostMapping("/api/users/{id}")
    fun update(@PathVariable id: String, @RequestBody updateUserDto: UpdateUserDto, principal: JwtAuthentication): ResponseEntity<ReturnUserDto>{
        val userId = try{
            UUID.fromString(id)
        } catch (ex: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }

        val hasAuthorityEditAll = principal.authorities.map { it.authority }.contains(Permissions.SERMANAGEMENT_SERVICE_EDIT_ALL_USER.name)
        val hasAuthorityEditOwn = principal.authorities.map { it.authority }.contains(Permissions.SERMANAGEMENT_SERVICE_EDIT_ALL_USER.name)
        if(!hasAuthorityEditAll || (hasAuthorityEditOwn && principal.getUserUuid() == userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        
        return userService.update(userId, updateUserDto)
            .let { userConverter.toDto(it) }
            .let { toResponseEntity(it) }
    }


    fun <T : Any> toResponseEntity(nullableEntity: T?): ResponseEntity<T> =
        nullableEntity?.let { entity ->
            ResponseEntity.ok(entity)
        } ?: ResponseEntity.badRequest().build()

}

