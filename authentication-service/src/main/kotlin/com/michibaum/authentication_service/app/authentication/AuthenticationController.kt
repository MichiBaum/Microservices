package com.michibaum.authentication_service.app.authentication

import com.michibaum.authentication_library.AuthenticationEndpoints
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.usermanagement_library.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController (
    private val authenticationService: AuthenticationService
) : AuthenticationEndpoints {

    @Autowired
    @Lazy
    lateinit var usermanagementClient: UsermanagementClient

    @PublicEndpoint
    @PostMapping(value = ["/api/authenticate"])
    fun authenticate(@RequestBody authenticationDto: AuthenticationDto): ResponseEntity<AuthenticationResponse> {
        val loginDto = LoginDto(authenticationDto.username, authenticationDto.password)
        val errors = LoginDtoValidator.validate(loginDto)
        if(errors.isNotEmpty())
            return ResponseEntity.badRequest().build()

        val userDetailsDto: UserDetailsDto = usermanagementClient.checkUserDetails(loginDto)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val jws = authenticationService.generateJWS(userDetailsDto)!!

        val responseBody = AuthenticationResponse(authenticationDto.username, jws)
        return ResponseEntity.ok()
            .body(responseBody)
    }

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

    override fun publicKey(): PublicKeyDto {
        return authenticationService.publicKey
    }
}