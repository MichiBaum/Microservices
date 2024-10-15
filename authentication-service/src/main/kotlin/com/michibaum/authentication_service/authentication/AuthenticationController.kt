package com.michibaum.authentication_service.authentication

import org.springframework.web.bind.annotation.RestController
import com.michibaum.authentication_library.AuthenticationEndpoints
import com.michibaum.usermanagement_library.UsermanagementClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import com.michibaum.usermanagement_library.LoginDto
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.usermanagement_library.UserDetailsDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestController
class AuthenticationController (
    val authenticationService: AuthenticationService
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
        val responseBody = AuthenticationResponse(authenticationDto.username, jws)
        return ResponseEntity.ok(responseBody)
    }

    override fun publicKey(): PublicKeyDto {
        return authenticationService.publicKey
    }
}