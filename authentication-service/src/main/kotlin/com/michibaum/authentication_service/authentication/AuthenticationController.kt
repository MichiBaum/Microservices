package com.michibaum.authentication_service.authentication

import org.springframework.web.bind.annotation.RestController
import com.michibaum.authentication_library.AuthenticationEndpoints
import com.michibaum.authentication_service.config.UsermanagementClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import com.michibaum.usermanagement_library.LoginDto
import com.michibaum.authentication_library.PublicKeyDto
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestController
class AuthenticationController (
    val authenticationService: AuthenticationService,
    val usermanagementClient: UsermanagementClient
) : AuthenticationEndpoints {

    @PostMapping(value = ["/authenticate"])
    fun authenticate(@RequestBody authenticationDto: AuthenticationDto): ResponseEntity<Any> {
        val loginDto = LoginDto(authenticationDto.username, authenticationDto.password)
        val passwordCorrect: Boolean = usermanagementClient.checkPassword(loginDto)

        if (!passwordCorrect)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val jws = authenticationService.generateJWS(loginDto.username)
        return ResponseEntity.ok().headers { i ->
            i.set(HttpHeaders.AUTHORIZATION, jws)
        }.build()
    }

    override fun publicKey(): PublicKeyDto {
        return authenticationService.publicKey
    }
}