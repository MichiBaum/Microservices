package com.michibaum.vpn_service.app.wireguard

import com.michibaum.vpn_service.config.kubernetes.KubernetesProperties
import io.fabric8.kubernetes.api.model.Service as K8sService
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

    fun createDeployment(requestedUser: String, namespace: String? = null): WireguardDeploymentDto {
        val client = kubernetesClient ?: throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE, "Kubernetes client is not available"
        )
        val targetNamespace = resolveNamespace(namespace)
        val sanitizedUser = sanitizeKubernetesName(requestedUser)

        val deploymentName = "wireguard-$sanitizedUser"
        val serviceName = "wireguard-$sanitizedUser-service"

        val existingDeployment = client.apps().deployments().inNamespace(targetNamespace).withName(deploymentName).get()
        val existingService = client.services().inNamespace(targetNamespace).withName(serviceName).get()

        if (existingDeployment != null || existingService != null) {
            if (existingDeployment != null) {
                return mapToDeploymentDto(existingDeployment, existingService)
            }
            throw ResponseStatusException(HttpStatus.CONFLICT, "WireGuard service already exists for user $sanitizedUser")
        }

        val serviceTemplateStream = javaClass.classLoader.getResourceAsStream("kubernetes-templates/wireguard-service.yaml")
            ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Wireguard service template not found"
            )
        val serviceTemplateContent = serviceTemplateStream.bufferedReader().use { it.readText() }
        val populatedServiceYaml = serviceTemplateContent
            .replace("\${userName}", sanitizedUser)
            .replace("\${namespace}", targetNamespace)

        val createdService = try {
            client.services().inNamespace(targetNamespace).load(populatedServiceYaml.byteInputStream()).create()
        } catch (e: Exception) {
            logger.error("Failed to create WireGuard service for user $sanitizedUser in namespace $targetNamespace", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create WireGuard service", e)
        }

        val externalPort = createdService.spec?.ports?.firstOrNull()?.nodePort?.takeIf { it > 0 }?.toString() ?: "51820"

        val deploymentTemplateStream = javaClass.classLoader.getResourceAsStream("kubernetes-templates/wireguard-deployment.yaml")
            ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Wireguard deployment template not found"
            )
        val deploymentTemplateContent = deploymentTemplateStream.bufferedReader().use { it.readText() }
        val populatedDeploymentYaml = deploymentTemplateContent
            .replace("\${userName}", sanitizedUser)
            .replace("\${namespace}", targetNamespace)
            .replace("\${serverPort}", externalPort)

        val createdDeployment = try {
            client.apps().deployments().inNamespace(targetNamespace).load(populatedDeploymentYaml.byteInputStream()).create()
        } catch (e: Exception) {
            logger.error("Failed to create WireGuard deployment for user $sanitizedUser in namespace $targetNamespace", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create WireGuard deployment", e)
        }

        return mapToDeploymentDto(createdDeployment, createdService)
    }

    fun getDeployment(requestedUser: String, namespace: String? = null): WireguardDeploymentDto? {
        val client = kubernetesClient ?: throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE, "Kubernetes client is not available"
        )
        val targetNamespace = resolveNamespace(namespace)
        val sanitizedUser = sanitizeKubernetesName(requestedUser)

        val deploymentName = "wireguard-$sanitizedUser"
        val serviceName = "wireguard-$sanitizedUser-service"

        val deployment = client.apps().deployments().inNamespace(targetNamespace).withName(deploymentName).get()
            ?: return null

        val service = client.services().inNamespace(targetNamespace).withName(serviceName).get()

        return mapToDeploymentDto(deployment, service)
    }

    fun deleteDeployment(requestedUser: String, namespace: String? = null) {
        val client = kubernetesClient ?: throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE, "Kubernetes client is not available"
        )
        val targetNamespace = resolveNamespace(namespace)
        val sanitizedUser = sanitizeKubernetesName(requestedUser)

        val deploymentName = "wireguard-$sanitizedUser"
        val serviceName = "wireguard-$sanitizedUser-service"

        try {
            client.apps().deployments().inNamespace(targetNamespace).withName(deploymentName).delete()
            client.services().inNamespace(targetNamespace).withName(serviceName).delete()
        } catch (e: Exception) {
            logger.error("Failed to delete WireGuard deployment or service for user $sanitizedUser in namespace $targetNamespace", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete WireGuard deployment or service", e)
        }
    }

    fun getPeerConfig(requestedUser: String, namespace: String? = null): String {
        val client = kubernetesClient ?: throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE, "Kubernetes client is not available"
        )
        val targetNamespace = resolveNamespace(namespace)
        val sanitizedUser = sanitizeKubernetesName(requestedUser)

        val pod = client.pods().inNamespace(targetNamespace)
            .withLabel("app", "wireguard")
            .withLabel("user", sanitizedUser)
            .list()
            .items
            .firstOrNull()
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "WireGuard pod for user $sanitizedUser not found"
            )

        val podName = pod.metadata?.name
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Pod name is missing")

        val filePath = "/config/peer_${sanitizedUser}/peer_${sanitizedUser}.conf"

        return try {
            val inputStream = client.pods()
                .inNamespace(targetNamespace)
                .withName(podName)
                .file(filePath)
                .read()
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            logger.error("Failed to read config file $filePath from pod $podName in namespace $targetNamespace", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read WireGuard config file", e)
        }
    }

    private fun sanitizeKubernetesName(name: String): String {
        val sanitized = name.lowercase().replace(Regex("[^a-z0-9-]"), "-").trim('-')
        return if (sanitized.isBlank()) "user" else sanitized.take(50)
    }

    private fun mapToDeploymentDto(deployment: Deployment, service: K8sService?): WireguardDeploymentDto {
        val servicePort = service?.spec?.ports?.firstOrNull()
        return WireguardDeploymentDto(
            name = deployment.metadata?.name ?: "",
            creationTimestamp = deployment.metadata?.creationTimestamp,
            containers = deployment.spec?.template?.spec?.containers?.mapNotNull { it.name } ?: emptyList(),
            port = servicePort?.port,
            nodePort = servicePort?.nodePort
        )
    }

    private fun resolveNamespace(namespace: String?): String =
        namespace?.takeIf { it.isNotBlank() }
            ?: kubernetesClient?.namespace?.takeIf { it.isNotBlank() }
            ?: kubernetesProperties.defaultNamespace
}
