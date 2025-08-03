package com.michibaum.authentication_service.app.basic_authentication

import com.michibaum.authentication_library.AuthenticationEndpoints
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_service.app.authentication.AuthenticationService
import com.michibaum.usermanagement_library.*
import io.micrometer.observation.annotation.Observed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController (
    private val authenticationService: AuthenticationService,
    private val authenticationAttemptService: AuthenticationAttemptService
) : AuthenticationEndpoints {

    @Autowired
    @Lazy
    lateinit var usermanagementClient: UsermanagementClient

    @Observed(name = "basic-authentication")
    @PublicEndpoint
    @PostMapping(value = ["/api/authenticate"])
    fun authenticate(@RequestBody authenticationDto: AuthenticationDto): ResponseEntity<AuthenticationResponse> {
        val loginDto = LoginDto(authenticationDto.username, authenticationDto.password)
        val errors = LoginDtoValidator.validate(loginDto)
        if(errors.isNotEmpty())
            return ResponseEntity.badRequest().build()

        val authenticationResult = authenticationAttemptService.createAttempt(loginDto.username)
        val userDetailsDto: UserDetailsDto? = usermanagementClient.checkUserDetails(loginDto)
        if(userDetailsDto == null){
            authenticationAttemptService.attemptFailed(authenticationResult)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val jws = authenticationService.generateJWS(userDetailsDto)!!

        authenticationAttemptService.attemptSuccessful(authenticationResult, userDetailsDto.id, jws)
        val responseBody = AuthenticationResponse(authenticationDto.username, jws)
        return ResponseEntity.ok()
            .body(responseBody)
    }

    override fun publicKey(): PublicKeyDto {
        return authenticationService.publicKey
    }
}