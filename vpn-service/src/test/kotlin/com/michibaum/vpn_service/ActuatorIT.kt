package com.michibaum.vpn_service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.permission_library.Permissions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
class ActuatorIT {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @MockitoBean
    lateinit var jwsValidator: JwsValidator

    @ParameterizedTest
    @ValueSource(strings = ["/actuator", "/actuator/health", "/actuator/info"])
    fun `actuator endpoints return 401`(endpoint: String) {
        val response = testRestTemplate.getForEntity(endpoint, String::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @ParameterizedTest
    @ValueSource(strings = ["/actuator", "/actuator/health", "/actuator/info"])
    fun `actuator endpoints with basic authentication return 200`(endpoint: String) {
        val response = testRestTemplate
            .withBasicAuth("admin_username", "admin_password")
            .getForEntity(endpoint, String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @ParameterizedTest
    @ValueSource(strings = ["/actuator", "/actuator/health", "/actuator/info"])
    fun `actuator endpoints with not enough authorities return 403`(endpoint: String) {
        val token = JWT.create()
            .withSubject("admin_username")
            .withClaim("userId", "isdnfvin")
            .withClaim("permissions", listOf(Permissions.CHESS_SERVICE.name))
            .sign(Algorithm.none())

        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val response = testRestTemplate.exchange(
            endpoint,
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }
}
