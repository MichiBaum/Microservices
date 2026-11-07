package com.michibaum.usermanagement_service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.permission_library.Permissions
import com.michibaum.usermanagement_library.CreateUserDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean

@AutoConfigureTestRestTemplate
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.boot.admin.service.username=admin_username",
        "spring.boot.admin.service.password=admin_password"
    ]
)
@TestcontainersConfiguration
class UserEndpointsSecurityIT {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @MockitoBean
    lateinit var jwsValidator: JwsValidator

    @Test
    fun `post users without authentication returns 401`() {
        val dto = CreateUserDto("test@example.com")
        val response = testRestTemplate.postForEntity("/api/users", dto, String::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `post users with insufficient permission returns 403`() {
        val token = JWT.create()
            .withSubject("user")
            .withClaim("userId", "123")
            .withClaim("permissions", listOf(Permissions.CHESS_SERVICE.name))
            .sign(Algorithm.none())

        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }
        val dto = CreateUserDto("test@example.com")
        val response = testRestTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            HttpEntity(dto, headers),
            String::class.java
        )

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `post users with authentication service permission is allowed`() {
        val token = JWT.create()
            .withSubject("authentication-service")
            .withClaim("userId", "authentication-service")
            .withClaim("permissions", listOf(Permissions.AUTHENTICATION_SERVICE.name))
            .sign(Algorithm.none())

        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }
        val dto = CreateUserDto("secure-user@example.com")
        val response = testRestTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            HttpEntity(dto, headers),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
