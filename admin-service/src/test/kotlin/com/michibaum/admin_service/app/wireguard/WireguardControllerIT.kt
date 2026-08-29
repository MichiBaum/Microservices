package com.michibaum.admin_service.app.wireguard

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.permission_library.Permissions
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.apps.DeploymentList
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.AppsAPIGroupDSL
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation
import io.fabric8.kubernetes.client.dsl.RollableScalableResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
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
class WireguardControllerIT {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @MockitoBean
    lateinit var jwsValidator: JwsValidator

    @MockitoBean
    lateinit var kubernetesClient: KubernetesClient

    @Test
    fun `create wireguard deployment without authentication returns 401`() {
        val response = testRestTemplate.postForEntity("/api/wireguard", null, String::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `create wireguard deployment with invalid permission returns 403`() {
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
            "/api/wireguard",
            HttpMethod.POST,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `create wireguard deployment without username returns 400`() {
        val token = JWT.create()
            .withSubject("anonymous")
            .withClaim("userId", "123")
            .withClaim("permissions", listOf(Permissions.ADMIN_SERVICE.name))
            .sign(Algorithm.none())

        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val response = testRestTemplate.exchange(
            "/api/wireguard",
            HttpMethod.POST,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `create wireguard deployment with ADMIN_SERVICE permission and valid user creates deployment`() {
        val token = JWT.create()
            .withSubject("john-doe")
            .withClaim("userId", "123")
            .withClaim("permissions", listOf(Permissions.ADMIN_SERVICE.name))
            .sign(Algorithm.none())

        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())

        val appsApi = Mockito.mock(AppsAPIGroupDSL::class.java)
        val deploymentsOp = Mockito.mock(MixedOperation::class.java) as MixedOperation<Deployment, DeploymentList, RollableScalableResource<Deployment>>
        val namespaceOp = Mockito.mock(NonNamespaceOperation::class.java) as NonNamespaceOperation<Deployment, DeploymentList, RollableScalableResource<Deployment>>
        val deploymentRes = Mockito.mock(RollableScalableResource::class.java) as RollableScalableResource<Deployment>

        val createdDeployment = Deployment().apply {
            metadata = ObjectMeta().apply {
                name = "wireguard-john-doe"
                namespace = "microservices"
            }
        }

        `when`(kubernetesClient.namespace).thenReturn("microservices")
        `when`(kubernetesClient.apps()).thenReturn(appsApi)
        `when`(appsApi.deployments()).thenReturn(deploymentsOp)
        `when`(deploymentsOp.inNamespace("microservices")).thenReturn(namespaceOp)
        `when`(namespaceOp.load(any(java.io.InputStream::class.java))).thenReturn(deploymentRes)
        `when`(deploymentRes.create()).thenReturn(createdDeployment)

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val response = testRestTemplate.exchange(
            "/api/wireguard",
            HttpMethod.POST,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.CREATED, response.statusCode)
    }
}
