package com.michibaum.authentication_service.app.basic_authentication

import com.michibaum.authentication_library.AuthenticationEndpoints
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_service.app.authentication.AuthenticationService
import com.michibaum.authentication_service.database.UserCredentialsRepository
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_library.UsermanagementClient
import feign.FeignException
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
class AuthenticationController (
    private val authenticationService: AuthenticationService,
    @Lazy private val usermanagementClient: UsermanagementClient,
    private val authenticationAttemptService: AuthenticationAttemptService,
    private val userCredentialsRepository: UserCredentialsRepository,
    private val passwordEncoder: PasswordEncoder
) : AuthenticationEndpoints {

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationController::class.java)
        private const val DUMMY_PASSWORD_HASH = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
    }

    @Observed(name = "basic-authentication")
    @PublicEndpoint
    @PostMapping(value = ["/api/basic-authentication/authenticate"])
    fun authenticate(@RequestBody authenticationDto: AuthenticationDto): ResponseEntity<AuthenticationResponse> {
        val errors = AuthenticationDtoValidator.validate(authenticationDto)
        if (errors.isNotEmpty())
            return ResponseEntity.badRequest().build()

        val authenticationAttempt = authenticationAttemptService.createAttempt(authenticationDto.username)

        val credentials = userCredentialsRepository.findByUsername(authenticationDto.username)
        if (credentials == null) {
            passwordEncoder.matches(authenticationDto.password, DUMMY_PASSWORD_HASH)
            authenticationAttemptService.attemptFailed(authenticationAttempt)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        if (!credentials.enabled || !passwordEncoder.matches(authenticationDto.password, credentials.passwordHash)) {
            authenticationAttemptService.attemptFailed(authenticationAttempt)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val userDetailsDto: UserDetailsDto? = try {
            usermanagementClient.getUserDetailsById(credentials.userId)
        } catch (ex: FeignException.NotFound) {
            logger.warn("User profile not found in usermanagement-service")
            null
        } catch (ex: FeignException) {
            logger.error("Error communicating with usermanagement-service: ${ex.message}")
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()
        } catch (ex: Exception) {
            logger.error("Unexpected error retrieving user details: ${ex.message}")
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()
        }

        if (userDetailsDto == null) {
            authenticationAttemptService.attemptFailed(authenticationAttempt)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val jws = authenticationService.generateJWS(userDetailsDto, credentials.username)

        authenticationAttemptService.attemptSuccessful(authenticationAttempt, userDetailsDto.id, jws)
        val responseBody = AuthenticationResponse(credentials.username, jws)
        return ResponseEntity.ok()
            .body(responseBody)
    }

    override fun publicKey(): PublicKeyDto {
        return authenticationService.publicKey
    }
}