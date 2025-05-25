package com.michibaum.gatewayservice.config

import de.codecentric.boot.admin.client.config.InstanceProperties
import de.codecentric.boot.admin.client.registration.ServletApplicationFactory
import de.codecentric.boot.admin.client.registration.metadata.MetadataContributor
import jakarta.servlet.ServletContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath
import org.springframework.util.StringUtils
import org.springframework.web.util.UriComponentsBuilder

class AdminClientServletApplicationFactory(
    val instance: InstanceProperties,
    val management: ManagementServerProperties,
    server: ServerProperties,
    servletContext: ServletContext,
    pathMappedEndpoints: PathMappedEndpoints,
    webEndpoint: WebEndpointProperties,
    metadataContributor: MetadataContributor,
    dispatcherServletPath: DispatcherServletPath
) : ServletApplicationFactory(
    instance,
    management,
    server,
    servletContext,
    pathMappedEndpoints,
    webEndpoint,
    metadataContributor,
    dispatcherServletPath
) {

    private val logger: Logger = LoggerFactory.getLogger(AdminClientServletApplicationFactory::class.java)

    override fun getManagementBaseUrl(): String {
        val baseUrl: String? = instance.managementBaseUrl

        if (baseUrl != null && StringUtils.hasText(baseUrl)) {
            logger.info("ManagementBaseUrl is configured to '{}'", baseUrl)
            return baseUrl
        }

        if (isManagementPortEqual) {
            logger.info("ManagementPort is configured to match ServerPort. Using server.port as management.port")
            return UriComponentsBuilder.fromHttpUrl(getServiceUrl())
                .path("/")
                .path(dispatcherServletPrefix)
                .path(managementContextPath)
                .toUriString()
        }

        logger.info("ManagementBaseUrl not configured. Trying to resolve it automatically")
        val ssl = management.ssl
        return UriComponentsBuilder.newInstance()
            .scheme(getScheme(ssl))
            .host(getManagementHost())
            .port(getLocalManagementPort())
            .path(managementContextPath)
            .toUriString()
    }
    
}