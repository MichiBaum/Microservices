package com.michibaum.authentication_service.authentication

import com.michibaum.usermanagement_library.UsermanagementClient
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class AuthenticationControllerUT {

    private val authenticationService: AuthenticationService = mockk()
    private val usermanagementClient: UsermanagementClient = mockk()

    private val authenticationController: AuthenticationController = AuthenticationController(authenticationService)
    init {
        authenticationController.usermanagementClient = usermanagementClient
    }

    @Test
    fun `Authentication successful`(){
        // GIVEN
        every { usermanagementClient.checkPassword(any()) } returns true

        val jws = "1234qwer"
        val authenticationDto = AuthenticationDto("UName", "Passwört")
        every { authenticationService.generateJWS(authenticationDto.username) } returns jws

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.headers)
        assertEquals(jws, result.headers[HttpHeaders.AUTHORIZATION]?.get(0) ?: "")
    }

    @Test
    fun `Bad credentials`(){
        // GIVEN
        every { usermanagementClient.checkPassword(any()) } returns false
        val authenticationDto = AuthenticationDto("UName", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        assertEquals(0, result.headers.size)
    }

}