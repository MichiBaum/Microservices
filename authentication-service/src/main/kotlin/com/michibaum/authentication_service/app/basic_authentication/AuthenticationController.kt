package com.michibaum.authentication_service.app.basic_authentication

import com.michibaum.authentication_library.AuthenticationEndpoints
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_service.app.authentication.AuthenticationService
import com.michibaum.authentication_service.database.UserCredentials
import com.michibaum.authentication_service.database.UserCredentialsRepository
import com.michibaum.usermanagement_library.*
import io.micrometer.observation.annotation.Observed
import org.springframework.context.annotation.Lazy
import org.springframework.http.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController (
    private val authenticationService: AuthenticationService,
    @Lazy private val usermanagementClient: UsermanagementClient,
    private val authenticationAttemptService: AuthenticationAttemptService,
    private val userCredentialsRepository: UserCredentialsRepository,
    private val passwordEncoder: PasswordEncoder
) : AuthenticationEndpoints {

    @Observed(name = "basic-authentication")
    @PublicEndpoint
    @PostMapping(value = ["/api/basic-authentication/authenticate"])
    fun authenticate(@RequestBody authenticationDto: AuthenticationDto): ResponseEntity<AuthenticationResponse> {
        val loginDto = LoginDto(authenticationDto.username, authenticationDto.password)
        val errors = LoginDtoValidator.validate(loginDto)
        if(errors.isNotEmpty())
            return ResponseEntity.badRequest().build()

        val authenticationAttempt = authenticationAttemptService.createAttempt(loginDto.username)

        val credentials = userCredentialsRepository.findByUsername(loginDto.username)
        val userDetailsDto: UserDetailsDto? = if (credentials != null) {
            if (!credentials.enabled || !passwordEncoder.matches(loginDto.password, credentials.passwordHash)) {
                null
            } else {
                usermanagementClient.getUserDetailsById(credentials.userId)
            }
        } else {
            val fallbackDetails = usermanagementClient.checkUserDetails(loginDto)
            if (fallbackDetails != null) {
                userCredentialsRepository.save(
                    UserCredentials(
                        userId = fallbackDetails.id,
                        username = loginDto.username,
                        passwordHash = passwordEncoder.encode(loginDto.password)!!,
                        enabled = true
                    )
                )
            }
            fallbackDetails
        }

        if(userDetailsDto == null){
            authenticationAttemptService.attemptFailed(authenticationAttempt)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val jws = authenticationService.generateJWS(userDetailsDto.copy(username = loginDto.username))!!

        authenticationAttemptService.attemptSuccessful(authenticationAttempt, userDetailsDto.id, jws)
        val responseBody = AuthenticationResponse(authenticationDto.username, jws)
        return ResponseEntity.ok()
            .body(responseBody)
    }

    override fun publicKey(): PublicKeyDto {
        return authenticationService.publicKey
    }
}