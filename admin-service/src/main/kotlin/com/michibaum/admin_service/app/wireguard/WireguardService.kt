package com.michibaum.admin_service.app.wireguard

import com.michibaum.admin_service.app.kubernetes.KubernetesProperties
import com.michibaum.admin_service.app.kubernetes.dto.DeploymentDto
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.client.KubernetesClient
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class WireguardService(
    private val kubernetesClient: KubernetesClient? = null,
    private val kubernetesProperties: KubernetesProperties
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createWireguardDeployment(requestedUser: String, namespace: String? = null): DeploymentDto {
        val client = kubernetesClient ?: throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE, "Kubernetes client is not available"
        )
        val targetNamespace = resolveNamespace(namespace)
        val sanitizedUser = sanitizeKubernetesName(requestedUser)

        val templateStream = javaClass.classLoader.getResourceAsStream("kubernetes-templates/wireguard.yaml")
            ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Wireguard pod template not found"
            )

        val templateContent = templateStream.bufferedReader().use { it.readText() }
        val populatedYaml = templateContent
            .replace("\${userName}", sanitizedUser)
            .replace("\${namespace}", targetNamespace)

        val createdDeployment = try {
            client.apps().deployments().inNamespace(targetNamespace).load(populatedYaml.byteInputStream()).create()
        } catch (e: Exception) {
            logger.error("Failed to create WireGuard deployment for user $sanitizedUser in namespace $targetNamespace", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create WireGuard deployment", e)
        }

        return mapToDeploymentDto(createdDeployment, targetNamespace)
    }

    private fun sanitizeKubernetesName(name: String): String {
        val sanitized = name.lowercase().replace(Regex("[^a-z0-9-]"), "-").trim('-')
        return if (sanitized.isBlank()) "user" else sanitized.take(50)
    }

    private fun mapToDeploymentDto(deployment: Deployment, targetNamespace: String): DeploymentDto =
        DeploymentDto(
            name = deployment.metadata?.name ?: "",
            namespace = deployment.metadata?.namespace ?: targetNamespace,
            replicas = deployment.spec?.replicas ?: deployment.status?.replicas,
            readyReplicas = deployment.status?.readyReplicas ?: 0,
            creationTimestamp = deployment.metadata?.creationTimestamp,
            containers = deployment.spec?.template?.spec?.containers?.mapNotNull { it.name } ?: emptyList()
        )

    private fun resolveNamespace(namespace: String?): String =
        namespace?.takeIf { it.isNotBlank() }
            ?: kubernetesClient?.namespace?.takeIf { it.isNotBlank() }
            ?: kubernetesProperties.defaultNamespace
}
