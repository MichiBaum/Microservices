package com.michibaum.authentication_service.basic_authentication

import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_service.app.authentication.AuthenticationService
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationAttemptService
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationController
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationDto
import com.michibaum.authentication_service.database.AuthenticationAttempt
import com.michibaum.authentication_service.database.JwtEntity
import com.michibaum.authentication_service.database.UserCredentials
import com.michibaum.authentication_service.database.UserCredentialsRepository
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationAttempt
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationFailure
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationSuccess
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_library.UsermanagementClient
import feign.FeignException
import feign.Request
import feign.RequestTemplate
import feign.RetryableException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class AuthenticationControllerUT {

    private val authenticationService: AuthenticationService = mockk()
    private val authenticationAttemptService: AuthenticationAttemptService = mockk()
    private val usermanagementClient: UsermanagementClient = mockk()
    private val userCredentialsRepository: UserCredentialsRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()

    private val authenticationController: AuthenticationController = AuthenticationController(
        authenticationService,
        usermanagementClient,
        authenticationAttemptService,
        userCredentialsRepository,
        passwordEncoder
    )

    @Test
    fun `Authentication successful with local credentials`() {
        // GIVEN
        val userDetailsDto = UserDetailsDto(
            id = "ubdf-sdfg-sdfv-sdfv",
            permissions = emptySet()
        )
        val userCredentials = UserCredentials("ubdf-sdfg-sdfv-sdfv", "UName", "hashedPassword")
        every { userCredentialsRepository.findByUsername("UName") } returns userCredentials
        every { passwordEncoder.matches("Passwört", "hashedPassword") } returns true
        every { usermanagementClient.getUserDetailsById("ubdf-sdfg-sdfv-sdfv") } returns userDetailsDto
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(AuthenticationAttempt(), firstArg()) }
        every { authenticationAttemptService.attemptSuccessful(any(), any(), any()) } answers { BasicAuthenticationSuccess(userDetailsDto.id, JwtEntity(jwt = thirdArg()), firstArg(), secondArg()) }

        val jws = "1234qwer"
        val authenticationDto = AuthenticationDto("UName", "Passwört")
        every { authenticationService.generateJWS(userDetailsDto, "UName") } returns jws

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(jws, result.body?.jwt ?: "")
        assertEquals(authenticationDto.username, result.body?.username ?: "")
        verify(exactly = 1) { usermanagementClient.getUserDetailsById("ubdf-sdfg-sdfv-sdfv") }
        verify(exactly = 1) { authenticationService.generateJWS(userDetailsDto, "UName") }
        verify(exactly = 1) { authenticationAttemptService.attemptSuccessful(any(), userDetailsDto.id, jws) }
        verify(exactly = 0) { authenticationAttemptService.attemptFailed(any()) }
    }

    @Test
    fun `Authentication fails when username not found in credentials repository without remote call`() {
        // GIVEN
        every { userCredentialsRepository.findByUsername("UName") } returns null
        every { passwordEncoder.matches("Passwört", any()) } returns false
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(AuthenticationAttempt(), firstArg()) }
        every { authenticationAttemptService.attemptFailed(any()) } answers { BasicAuthenticationFailure(firstArg(), firstArg<BasicAuthenticationAttempt>().username) }
        val authenticationDto = AuthenticationDto("UName", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        verify(exactly = 1) { authenticationAttemptService.attemptFailed(any()) }
        verify(exactly = 0) { usermanagementClient.getUserDetailsById(any()) }
        verify(exactly = 1) { passwordEncoder.matches("Passwört", any()) }
        verify(exactly = 0) { authenticationService.generateJWS(any(), any()) }
    }

    @Test
    fun `Authentication fails when account is disabled without remote call`() {
        // GIVEN
        val userCredentials = UserCredentials("ubdf-sdfg-sdfv-sdfv", "UName", "hashedPassword", enabled = false)
        every { userCredentialsRepository.findByUsername("UName") } returns userCredentials
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(AuthenticationAttempt(), firstArg()) }
        every { authenticationAttemptService.attemptFailed(any()) } answers { BasicAuthenticationFailure(firstArg(), firstArg<BasicAuthenticationAttempt>().username) }
        val authenticationDto = AuthenticationDto("UName", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        verify(exactly = 1) { authenticationAttemptService.attemptFailed(any()) }
        verify(exactly = 0) { usermanagementClient.getUserDetailsById(any()) }
        verify(exactly = 0) { authenticationService.generateJWS(any(), any()) }
    }

    @Test
    fun `Authentication fails when password does not match without remote call`() {
        // GIVEN
        val userCredentials = UserCredentials("ubdf-sdfg-sdfv-sdfv", "UName", "hashedPassword", enabled = true)
        every { userCredentialsRepository.findByUsername("UName") } returns userCredentials
        every { passwordEncoder.matches("WrongPassword", "hashedPassword") } returns false
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(AuthenticationAttempt(), firstArg()) }
        every { authenticationAttemptService.attemptFailed(any()) } answers { BasicAuthenticationFailure(firstArg(), firstArg<BasicAuthenticationAttempt>().username) }
        val authenticationDto = AuthenticationDto("UName", "WrongPassword")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        verify(exactly = 1) { authenticationAttemptService.attemptFailed(any()) }
        verify(exactly = 0) { usermanagementClient.getUserDetailsById(any()) }
        verify(exactly = 0) { authenticationService.generateJWS(any(), any()) }
    }

    @Test
    fun `Authentication fails when user profile is missing in usermanagement`() {
        // GIVEN
        val userCredentials = UserCredentials("ubdf-sdfg-sdfv-sdfv", "UName", "hashedPassword", enabled = true)
        every { userCredentialsRepository.findByUsername("UName") } returns userCredentials
        every { passwordEncoder.matches("Passwört", "hashedPassword") } returns true
        every { usermanagementClient.getUserDetailsById("ubdf-sdfg-sdfv-sdfv") } returns null
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(AuthenticationAttempt(), firstArg()) }
        every { authenticationAttemptService.attemptFailed(any()) } answers { BasicAuthenticationFailure(firstArg(), firstArg<BasicAuthenticationAttempt>().username) }
        val authenticationDto = AuthenticationDto("UName", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        verify(exactly = 1) { usermanagementClient.getUserDetailsById("ubdf-sdfg-sdfv-sdfv") }
        verify(exactly = 1) { authenticationAttemptService.attemptFailed(any()) }
        verify(exactly = 0) { authenticationService.generateJWS(any(), any()) }
    }

    @Test
    fun `Authentication fails with 401 when usermanagement client throws Feign NotFound`() {
        // GIVEN
        val userCredentials = UserCredentials("ubdf-sdfg-sdfv-sdfv", "UName", "hashedPassword", enabled = true)
        val request = Request.create(Request.HttpMethod.GET, "/api/users/ubdf-sdfg-sdfv-sdfv", emptyMap(), null, RequestTemplate())
        every { userCredentialsRepository.findByUsername("UName") } returns userCredentials
        every { passwordEncoder.matches("Passwört", "hashedPassword") } returns true
        every { usermanagementClient.getUserDetailsById("ubdf-sdfg-sdfv-sdfv") } throws FeignException.NotFound("Not Found", request, null, emptyMap())
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(AuthenticationAttempt(), firstArg()) }
        every { authenticationAttemptService.attemptFailed(any()) } answers { BasicAuthenticationFailure(firstArg(), firstArg<BasicAuthenticationAttempt>().username) }
        val authenticationDto = AuthenticationDto("UName", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        verify(exactly = 1) { usermanagementClient.getUserDetailsById("ubdf-sdfg-sdfv-sdfv") }
        verify(exactly = 1) { authenticationAttemptService.attemptFailed(any()) }
        verify(exactly = 0) { authenticationService.generateJWS(any(), any()) }
    }

    @Test
    fun `Authentication fails with 503 without credential failure when usermanagement client throws 5xx server error`() {
        // GIVEN
        val userCredentials = UserCredentials("ubdf-sdfg-sdfv-sdfv", "UName", "hashedPassword", enabled = true)
        val request = Request.create(Request.HttpMethod.GET, "/api/users/ubdf-sdfg-sdfv-sdfv", emptyMap(), null, RequestTemplate())
        every { userCredentialsRepository.findByUsername("UName") } returns userCredentials
        every { passwordEncoder.matches("Passwört", "hashedPassword") } returns true
        every { usermanagementClient.getUserDetailsById("ubdf-sdfg-sdfv-sdfv") } throws FeignException.InternalServerError("Server Error", request, null, emptyMap())
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(AuthenticationAttempt(), firstArg()) }
        val authenticationDto = AuthenticationDto("UName", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.statusCode)
        verify(exactly = 1) { usermanagementClient.getUserDetailsById("ubdf-sdfg-sdfv-sdfv") }
        verify(exactly = 0) { authenticationAttemptService.attemptFailed(any()) }
        verify(exactly = 0) { authenticationService.generateJWS(any(), any()) }
    }

    @Test
    fun `Authentication fails with 503 without credential failure when usermanagement client encounters transport failure`() {
        // GIVEN
        val userCredentials = UserCredentials("ubdf-sdfg-sdfv-sdfv", "UName", "hashedPassword", enabled = true)
        val request = Request.create(Request.HttpMethod.GET, "/api/users/ubdf-sdfg-sdfv-sdfv", emptyMap(), null, RequestTemplate())
        every { userCredentialsRepository.findByUsername("UName") } returns userCredentials
        every { passwordEncoder.matches("Passwört", "hashedPassword") } returns true
        every { usermanagementClient.getUserDetailsById("ubdf-sdfg-sdfv-sdfv") } throws RetryableException(503, "Service Unavailable", Request.HttpMethod.GET, null as Throwable?, null as Long?, request)
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(AuthenticationAttempt(), firstArg()) }
        val authenticationDto = AuthenticationDto("UName", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.statusCode)
        verify(exactly = 1) { usermanagementClient.getUserDetailsById("ubdf-sdfg-sdfv-sdfv") }
        verify(exactly = 0) { authenticationAttemptService.attemptFailed(any()) }
        verify(exactly = 0) { authenticationService.generateJWS(any(), any()) }
    }

    @Test
    fun `dummy password hash is valid bcrypt hash compatible with BCryptPasswordEncoder`() {
        val encoder = BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.`$2A`, 10, null)
        val matches = encoder.matches("randomPassword123", "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
        assertFalse(matches)
    }

    @Test
    fun `Validation fails with empty username`() {
        // GIVEN
        val authenticationDto = AuthenticationDto("", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(exactly = 0) { userCredentialsRepository.findByUsername(any()) }
        verify(exactly = 0) { authenticationAttemptService.createAttempt(any()) }
    }

    @Test
    fun `Validation fails with whitespace username`() {
        // GIVEN
        val authenticationDto = AuthenticationDto("   ", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(exactly = 0) { userCredentialsRepository.findByUsername(any()) }
        verify(exactly = 0) { authenticationAttemptService.createAttempt(any()) }
    }

    @Test
    fun `Validation fails with empty password`() {
        // GIVEN
        val authenticationDto = AuthenticationDto("UName", "")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(exactly = 0) { userCredentialsRepository.findByUsername(any()) }
        verify(exactly = 0) { authenticationAttemptService.createAttempt(any()) }
    }

    @Test
    fun `Validation fails with whitespace password`() {
        // GIVEN
        val authenticationDto = AuthenticationDto("UName", "   ")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(exactly = 0) { userCredentialsRepository.findByUsername(any()) }
        verify(exactly = 0) { authenticationAttemptService.createAttempt(any()) }
    }

    @Test
    fun `Public key returns correctly`() {
        // GIVEN
        val publicKeyDto = PublicKeyDto("RSA", "key".toByteArray())
        every { authenticationService.publicKey } returns publicKeyDto

        // WHEN
        val result = authenticationController.publicKey()

        // THEN
        assertArrayEquals(publicKeyDto.key, result.key)
        assertEquals(publicKeyDto.algorithm, result.algorithm)
    }

}