package com.michibaum.admin_service.app.wireguard

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.permission_library.Permissions
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.api.model.PodList
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.ServiceList
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.apps.DeploymentList
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.AppsAPIGroupDSL
import io.fabric8.kubernetes.client.dsl.CopyOrReadable
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation
import io.fabric8.kubernetes.client.dsl.PodResource
import io.fabric8.kubernetes.client.dsl.Resource
import io.fabric8.kubernetes.client.dsl.RollableScalableResource
import io.fabric8.kubernetes.client.dsl.ServiceResource
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
        val deploymentNamespaceOp = Mockito.mock(NonNamespaceOperation::class.java) as NonNamespaceOperation<Deployment, DeploymentList, RollableScalableResource<Deployment>>
        val deploymentRes = Mockito.mock(RollableScalableResource::class.java) as RollableScalableResource<Deployment>

        val servicesOp = Mockito.mock(MixedOperation::class.java) as MixedOperation<Service, ServiceList, ServiceResource<Service>>
        val serviceNamespaceOp = Mockito.mock(NonNamespaceOperation::class.java) as NonNamespaceOperation<Service, ServiceList, ServiceResource<Service>>
        val serviceRes = Mockito.mock(ServiceResource::class.java) as ServiceResource<Service>

        val createdDeployment = Deployment().apply {
            metadata = ObjectMeta().apply {
                name = "wireguard-john-doe"
                namespace = "microservices"
            }
        }
        val createdService = Service().apply {
            metadata = ObjectMeta().apply {
                name = "wireguard-john-doe-service"
                namespace = "microservices"
            }
        }

        `when`(kubernetesClient.namespace).thenReturn("microservices")
        `when`(kubernetesClient.apps()).thenReturn(appsApi)
        `when`(appsApi.deployments()).thenReturn(deploymentsOp)
        `when`(deploymentsOp.inNamespace("microservices")).thenReturn(deploymentNamespaceOp)
        `when`(deploymentNamespaceOp.load(any(java.io.InputStream::class.java))).thenReturn(deploymentRes)
        `when`(deploymentRes.create()).thenReturn(createdDeployment)

        `when`(kubernetesClient.services()).thenReturn(servicesOp)
        `when`(servicesOp.inNamespace("microservices")).thenReturn(serviceNamespaceOp)
        `when`(serviceNamespaceOp.load(any(java.io.InputStream::class.java))).thenReturn(serviceRes)
        `when`(serviceRes.create()).thenReturn(createdService)

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

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `get wireguard config with ADMIN_SERVICE permission and valid user returns config`() {
        val token = JWT.create()
            .withSubject("john-doe")
            .withClaim("userId", "123")
            .withClaim("permissions", listOf(Permissions.ADMIN_SERVICE.name))
            .sign(Algorithm.none())

        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())

        val podsOp = Mockito.mock(MixedOperation::class.java) as MixedOperation<Pod, PodList, PodResource>
        val namespaceOp = Mockito.mock(NonNamespaceOperation::class.java) as NonNamespaceOperation<Pod, PodList, PodResource>
        val podRes = Mockito.mock(PodResource::class.java)
        val copyOrReadable = Mockito.mock(CopyOrReadable::class.java)

        val pod = Pod().apply {
            metadata = ObjectMeta().apply {
                name = "wireguard-john-doe-12345"
                namespace = "microservices"
            }
        }
        val podList = PodList().apply { items = listOf(pod) }

        `when`(kubernetesClient.namespace).thenReturn("microservices")
        `when`(kubernetesClient.pods()).thenReturn(podsOp)
        `when`(podsOp.inNamespace("microservices")).thenReturn(namespaceOp)
        `when`(namespaceOp.withLabel("app", "wireguard")).thenReturn(namespaceOp)
        `when`(namespaceOp.withLabel("user", "john-doe")).thenReturn(namespaceOp)
        `when`(namespaceOp.list()).thenReturn(podList)

        `when`(namespaceOp.withName("wireguard-john-doe-12345")).thenReturn(podRes)
        `when`(podRes.file("/config/peer_john-doe/peer_john-doe.conf")).thenReturn(copyOrReadable)
        `when`(copyOrReadable.read()).thenReturn("[Interface]\nAddress = 10.13.13.2/32".byteInputStream())

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val response = testRestTemplate.exchange(
            "/api/wireguard/config",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("[Interface]\nAddress = 10.13.13.2/32", response.body)
    }
}
