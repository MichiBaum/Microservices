package com.michibaum.authentication_starter

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_library.public_endpoints.PublicEndpointResolver
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfiguration
@EnableConfigurationProperties(value = [PublicEndpointProperties::class])
class PublicEndpointAutoConfiguration {

    @Bean
    fun publicEndpointResolver(publicEndpointProperties: PublicEndpointProperties): PublicEndpointResolver =
        PublicEndpointResolver(PublicEndpoint::class.java, *publicEndpointProperties.packageNames.toTypedArray()).apply {
            addStaticPublicEndpoints(publicEndpointProperties.endpoints)
        }

}