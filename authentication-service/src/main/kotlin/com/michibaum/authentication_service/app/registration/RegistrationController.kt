package com.michibaum.authentication_service.app.registration

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_service.database.UserCredentials
import com.michibaum.authentication_service.database.UserCredentialsRepository
import com.michibaum.usermanagement_library.CreateUserDto
import com.michibaum.usermanagement_library.UsermanagementClient
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RegistrationController(
    @Lazy private val usermanagementClient: UsermanagementClient,
    private val userCredentialsRepository: UserCredentialsRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PublicEndpoint
    @PostMapping(value = ["/api/register"])
    fun register(@RequestBody registerDto: RegisterDto): ResponseEntity<RegisterResponse> {
        val errors = RegisterDtoValidator.validate(registerDto)
        if(errors.isNotEmpty())
            return ResponseEntity.badRequest().build()

        if(userCredentialsRepository.existsByUsername(registerDto.username)) {
            val responseBody = RegisterResponse(RegisterState.ERROR, registerDto.username, registerDto.email)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody)
        }

        val createUserDto = CreateUserDto(registerDto.username, registerDto.email, registerDto.password)
        val createdUser = try {
            usermanagementClient.create(createUserDto)
        } catch (_: Exception) {
            null
        }

        if(createdUser == null) {
            val responseBody = RegisterResponse(RegisterState.ERROR, registerDto.username, registerDto.email)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody)
        }

        try {
            userCredentialsRepository.save(
                UserCredentials(
                    userId = createdUser.id,
                    username = registerDto.username,
                    passwordHash = passwordEncoder.encode(registerDto.password)!!,
                    enabled = true
                )
            )
        } catch (_: Exception) {
            try {
                usermanagementClient.delete(createdUser.id)
            } catch (_: Exception) {
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }

        val responseBody = RegisterResponse(RegisterState.SUCCESS, registerDto.username, registerDto.email)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody)
    }

}