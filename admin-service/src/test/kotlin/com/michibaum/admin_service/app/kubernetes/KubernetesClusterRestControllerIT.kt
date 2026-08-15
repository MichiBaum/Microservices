package com.michibaum.admin_service.app.kubernetes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.permission_library.Permissions
import io.fabric8.kubernetes.client.KubernetesClient
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
class KubernetesClusterRestControllerIT {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @MockitoBean
    lateinit var jwsValidator: JwsValidator

    @MockitoBean
    lateinit var kubernetesClient: KubernetesClient

    @Test
    fun `get pods without authentication returns 401`() {
        val response = testRestTemplate.getForEntity("/api/k8s/pods", String::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `get pods with invalid permission returns 403`() {
        val token = JWT.create()
            .withSubject("user")
            .withClaim("userId", "123")
            .withClaim("permissions", listOf(Permissions.CHESS_SERVICE.name))
            .sign(Algorithm.none())

        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val response = testRestTemplate.exchange(
            "/api/k8s/pods",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `get pods with ADMIN_SERVICE permission returns 200`() {
        val token = JWT.create()
            .withSubject("admin")
            .withClaim("userId", "123")
            .withClaim("permissions", listOf(Permissions.ADMIN_SERVICE.name))
            .sign(Algorithm.none())

        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val response = testRestTemplate.exchange(
            "/api/k8s/pods",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
