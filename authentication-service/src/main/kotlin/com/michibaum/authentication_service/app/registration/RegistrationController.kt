package com.michibaum.authentication_service.app.registration

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_service.database.UserCredentials
import com.michibaum.authentication_service.database.UserCredentialsRepository
import com.michibaum.usermanagement_library.CreateUserDto
import com.michibaum.usermanagement_library.UsermanagementClient
import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory
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

    companion object {
        private val logger = LoggerFactory.getLogger(RegistrationController::class.java)
    }

    @Observed(name = "registration")
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

        val createUserDto = CreateUserDto(registerDto.email)
        val createdUser = try {
            usermanagementClient.create(createUserDto)
        } catch (ex: Exception) {
            logger.error("Failed to create user in usermanagement-service for email ${registerDto.email}: ${ex.message}", ex)
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
        } catch (ex: Exception) {
            logger.error("Failed to save credentials for user ${createdUser.id}, executing compensating rollback: ${ex.message}", ex)
            try {
                usermanagementClient.delete(createdUser.id)
                logger.info("Successfully compensated by deleting orphaned user ${createdUser.id} in usermanagement-service")
            } catch (deleteEx: Exception) {
                logger.error("Compensating action failed: unable to delete user ${createdUser.id} in usermanagement-service: ${deleteEx.message}", deleteEx)
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }

        val responseBody = RegisterResponse(RegisterState.SUCCESS, registerDto.username, registerDto.email)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody)
    }

}