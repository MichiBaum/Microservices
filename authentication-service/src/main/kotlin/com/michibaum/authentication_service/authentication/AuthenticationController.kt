package com.michibaum.authentication_service.authentication

import com.michibaum.authentication_library.AuthenticationEndpoints
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.usermanagement_library.CreateUserDto
import com.michibaum.usermanagement_library.LoginDto
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_library.UsermanagementClient
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
        val userDetailsDto: UserDetailsDto = usermanagementClient.checkUserDetails(loginDto)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val jws = authenticationService.generateJWS(userDetailsDto)!!

        val cookie = ResponseCookie.from("jwt", jws)
            .httpOnly(true)
            .maxAge(Duration.ofHours(8))
            .domain("michibaum.ch")
            .secure(true)
            .build()

        val responseBody = AuthenticationResponse(authenticationDto.username, jws)
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(responseBody)
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
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }

    @PostMapping(value = ["/api/register"])
    fun register(@RequestBody registerDto: RegisterDto): ResponseEntity<RegisterResponse> {
        val createUserDto = CreateUserDto(registerDto.username, registerDto.email, registerDto.password)

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