package com.michibaum.authentication_service.authentication

import com.michibaum.authentication_library.AuthenticationEndpoints
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.usermanagement_library.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
class AuthenticationController (
    private val authenticationService: AuthenticationService
) : AuthenticationEndpoints {

    @Autowired
    @Lazy
    lateinit var usermanagementClient: UsermanagementClient

    @PostMapping(value = ["/api/authenticate"])
    fun authenticate(@RequestBody authenticationDto: AuthenticationDto): ResponseEntity<AuthenticationResponse> {
        val loginDto = LoginDto(authenticationDto.username, authenticationDto.password)
        val errors = LoginDtoValidator.validate(loginDto)
        if(errors.isNotEmpty())
            return ResponseEntity.badRequest().build()

        val userDetailsDto: UserDetailsDto = usermanagementClient.checkUserDetails(loginDto)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val jws = authenticationService.generateJWS(userDetailsDto)!!

        val cookie = ResponseCookie.from("jwt", jws)
            .httpOnly(true)
            .maxAge(Duration.ofHours(8))
            .domain("michibaum.ch")
            .secure(true)
            .sameSite("Lax")
            .build()

        val responseBody = AuthenticationResponse(authenticationDto.username, jws)
        return ResponseEntity.ok()
            .headers {
                it.set(HttpHeaders.SET_COOKIE, cookie.toString())
                it.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
            }
            .body(responseBody)
    }

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

    @PostMapping(value = ["/api/logout"])
    fun logout(): ResponseEntity<Any> {
        val cookie = ResponseCookie.from("jwt")
            .maxAge(0)
            .httpOnly(true)
            .maxAge(Duration.ofHours(8))
            .domain("michibaum.ch")
            .secure(true)
            .build()
        return ResponseEntity.ok()
            .headers {
                it.set(HttpHeaders.SET_COOKIE, cookie.toString())
                it.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
            }
            .build()
    }

    override fun publicKey(): PublicKeyDto {
        return authenticationService.publicKey
    }
}