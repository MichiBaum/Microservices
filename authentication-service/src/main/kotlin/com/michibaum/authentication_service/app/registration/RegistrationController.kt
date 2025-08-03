package com.michibaum.authentication_service.app.registration

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.usermanagement_library.CreateUserDto
import com.michibaum.usermanagement_library.CreateUserDtoValidator
import com.michibaum.usermanagement_library.UsermanagementClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RegistrationController(
    
) {

    @Autowired
    @Lazy
    lateinit var usermanagementClient: UsermanagementClient

    @PublicEndpoint
    @PostMapping(value = ["/api/register"])
    fun register(@RequestBody registerDto: RegisterDto): ResponseEntity<RegisterResponse> {
        val createUserDto = CreateUserDto(registerDto.username, registerDto.email, registerDto.password)
        val errors = CreateUserDtoValidator.validate(createUserDto)
        if(errors.isNotEmpty())
            return ResponseEntity.badRequest().build()

        val result = try {
            usermanagementClient.create(createUserDto)
        } catch (e: Exception) {
            null
        }

        if(result != null) {
            val responseBody = RegisterResponse(RegisterState.SUCCESS, registerDto.username, registerDto.email)
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody)
        }
        val responseBody = RegisterResponse(RegisterState.ERROR, registerDto.username, registerDto.email)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody)
    }
    
}