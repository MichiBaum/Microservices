package com.michibaum.authentication_service.authentication

import org.springframework.web.bind.annotation.RestController
import lombok.RequiredArgsConstructor
import com.michibaum.authentication_library.AuthenticationEndpoints
import com.michibaum.authentication_service.config.UsermanagementClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import com.michibaum.usermanagement_library.LoginDto
import com.michibaum.authentication_library.PublicKeyDto

@RestController
@RequiredArgsConstructor
internal class AuthenticationController (
    val authenticationService: AuthenticationService,
    val usermanagementClient: UsermanagementClient
) : AuthenticationEndpoints {

    @PostMapping(value = ["/authenticate"])
    fun authenticate(@RequestBody authenticationDto: AuthenticationDto) {
        val loginDto = LoginDto(authenticationDto.username, authenticationDto.password)
        val passwordCorrect: Boolean = usermanagementClient.checkPassword(loginDto)
        if (passwordCorrect) {
            authenticationService.generateJWS()
        }
    }

    @get:Override
    override val publicKey: PublicKeyDto
        get() = authenticationService.publicKey
}