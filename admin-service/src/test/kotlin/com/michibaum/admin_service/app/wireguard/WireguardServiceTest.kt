package com.michibaum.admin_service.app.wireguard

import com.michibaum.admin_service.app.kubernetes.KubernetesProperties
import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.PodSpec
import io.fabric8.kubernetes.api.model.PodTemplateSpec
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.apps.DeploymentList
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation
import io.fabric8.kubernetes.client.dsl.RollableScalableResource
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException

class WireguardServiceTest {

    private val kubernetesClient = mockk<KubernetesClient>()
    private val kubernetesProperties = KubernetesProperties()
    private val service = WireguardService(kubernetesClient, kubernetesProperties)

    @Test
    fun `createWireguardDeployment throws 503 when kubernetesClient is null`() {
        val nullClientService = WireguardService(null, kubernetesProperties)

        assertThrows<ResponseStatusException> {
            nullClientService.createWireguardDeployment("testuser")
        }
    }

    @Test
    fun `createWireguardDeployment creates deployment successfully`() {
        val deploymentsOperation = mockk<MixedOperation<Deployment, DeploymentList, RollableScalableResource<Deployment>>>()
        val deploymentNamespaceOperation = mockk<NonNamespaceOperation<Deployment, DeploymentList, RollableScalableResource<Deployment>>>()
        val deploymentResource = mockk<RollableScalableResource<Deployment>>()

        val createdDeployment = Deployment().apply {
            metadata = ObjectMeta().apply {
                name = "wireguard-testuser"
                namespace = "microservices"
            }
            spec = DeploymentSpec().apply {
                replicas = 1
                template = PodTemplateSpec().apply {
                    spec = PodSpec().apply {
                        containers = listOf(Container().apply { name = "wireguard" })
                    }
                }
            }
            status = DeploymentStatus().apply {
                readyReplicas = 1
            }
        }

        every { kubernetesClient.namespace } returns "microservices"
        every { kubernetesClient.apps().deployments() } returns deploymentsOperation
        every { deploymentsOperation.inNamespace("microservices") } returns deploymentNamespaceOperation
        every { deploymentNamespaceOperation.load(any<java.io.InputStream>()) } returns deploymentResource
        every { deploymentResource.create() } returns createdDeployment

        val result = service.createWireguardDeployment("testuser")

        assertEquals("wireguard-testuser", result.name)
        assertEquals("microservices", result.namespace)
        assertEquals(1, result.replicas)
        assertEquals(1, result.readyReplicas)
        assertEquals(listOf("wireguard"), result.containers)
    }
}
