package com.michibaum.usermanagement_service

import org.springframework.web.bind.annotation.RestController
import lombok.RequiredArgsConstructor
import com.michibaum.usermanagement_library.UserManagementEndpoints
import com.michibaum.usermanagement_library.LoginDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.Optional

@RestController
@RequiredArgsConstructor
class UsermanagementController (
    private val userService: UserService
) : UserManagementEndpoints {

    override fun checkPassword(loginDto: LoginDto): Boolean {
        return true
    }

    @GetMapping("/user/{id}")
    fun get(@PathVariable id: String) =
        userService.getUser(id)
            .let { convertUserToDto(it) }
            .let { toResponseEntity(it) }

    @PostMapping("/user/{id}")
    fun update(@PathVariable id: String, @RequestBody updateUserDto: UpdateUserDto) =
        userService.update(id, updateUserDto)
            .let { toResponseEntity(it) }
}

fun convertUserToDto(user: User?) =
    user?.let {
        ReturnUserDto(user.id, user.username, user.email)
    }

fun <T> Optional<T>.convertToNullable(): T? =
    this.get()

fun <T> toResponseEntity(nullableEntity: T?): ResponseEntity<T> =
    nullableEntity?.let {
        ResponseEntity.ok(it)
    } ?: ResponseEntity.badRequest().build()
