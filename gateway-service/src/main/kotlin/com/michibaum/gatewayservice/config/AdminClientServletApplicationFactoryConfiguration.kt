package com.michibaum.gatewayservice.config

import de.codecentric.boot.admin.client.config.ClientProperties
import de.codecentric.boot.admin.client.config.InstanceProperties
import de.codecentric.boot.admin.client.config.SpringBootAdminClientAutoConfiguration
import de.codecentric.boot.admin.client.registration.ApplicationFactory
import de.codecentric.boot.admin.client.registration.metadata.CompositeMetadataContributor
import de.codecentric.boot.admin.client.registration.metadata.MetadataContributor
import jakarta.servlet.ServletContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
@EnableConfigurationProperties(ClientProperties::class, InstanceProperties::class, ServerProperties::class, ManagementServerProperties::class)
@AutoConfigureBefore(value = [SpringBootAdminClientAutoConfiguration.ServletConfiguration::class])
class AdminClientServletApplicationFactoryConfiguration {

    private val logger: Logger = LoggerFactory.getLogger(AdminClientServletApplicationFactoryConfiguration::class.java)
    
    @Bean
    @Primary
    fun applicationFactory(
        instance: InstanceProperties,
        management: ManagementServerProperties,
        server: ServerProperties,
        servletContext: ServletContext,
        pathMappedEndpoints: PathMappedEndpoints,
        webEndpoint: WebEndpointProperties,
        metadataContributors: ObjectProvider<List<MetadataContributor>>,
        dispatcherServletPath: DispatcherServletPath
    ): ApplicationFactory {
        logger.info("Registering Admin Client Servlet Application Factory")
        return AdminClientServletApplicationFactory(
            instance, 
            management, 
            server, 
            servletContext, 
            pathMappedEndpoints, 
            webEndpoint,
            CompositeMetadataContributor(metadataContributors.getIfAvailable { mutableListOf() }), 
            dispatcherServletPath
        )
    }
    
}