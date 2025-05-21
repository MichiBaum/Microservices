package com.michibaum.gatewayservice.config

import de.codecentric.boot.admin.client.config.InstanceProperties
import de.codecentric.boot.admin.client.registration.ServletApplicationFactory
import de.codecentric.boot.admin.client.registration.metadata.MetadataContributor
import jakarta.servlet.ServletContext
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
    metadataContributors: MetadataContributor,
    dispatcherServletPath: DispatcherServletPath
) : ServletApplicationFactory(
    instance,
    management,
    server,
    servletContext,
    pathMappedEndpoints,
    webEndpoint,
    metadataContributors,
    dispatcherServletPath
) {

    override fun getManagementBaseUrl(): String {
        val baseUrl: String? = instance.managementBaseUrl

        if (baseUrl != null && StringUtils.hasText(baseUrl)) {
            return baseUrl
        }

        if (isManagementPortEqual) {
            return UriComponentsBuilder.fromHttpUrl(getServiceUrl())
                .path("/")
                .path(dispatcherServletPrefix)
                .path(managementContextPath)
                .toUriString()
        }

        val ssl = management.ssl
        return UriComponentsBuilder.newInstance()
            .scheme(getScheme(ssl))
            .host(getManagementHost())
            .port(getLocalManagementPort())
            .path(managementContextPath)
            .toUriString()
    }
    
}