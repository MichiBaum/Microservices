package com.michibaum.lifemanagementbackend

import com.michibaum.lifemanagementbackend.core.config.PropertyLogger
import com.michibaum.lifemanagementbackend.core.publicendpoint.PublicEndpoint
import com.michibaum.lifemanagementbackend.core.publicendpoint.PublicEndpointDetails
import com.michibaum.lifemanagementbackend.core.publicendpoint.PublicEndpointSearcher
import io.swagger.v3.core.model.ApiDescription
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@SpringBootApplication
@PropertySources(
    PropertySource("classpath:application.properties")
)
class LifemanagementBackendApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication(LifemanagementBackendApplication::class.java).apply {
                addListeners(PropertyLogger())
                run(*args)
            }
        }
    }

    @Bean
    fun bCryptPasswordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun publicEndpoints(): List<PublicEndpointDetails> {
        val publicEndpointSearcher = PublicEndpointSearcher("com.michibaum.lifemanagementbackend")
        val restControllers: List<Class<*>> = publicEndpointSearcher.allRestController

        return restControllers
            .map { clazz: Class<*> ->
                publicEndpointSearcher.getMethodsAnnotatedWith(
                    clazz,
                    PublicEndpoint::class.java
                )
            }
            .flatten()
            .map(publicEndpointSearcher::getRequestMappingValue)
            .flatten()
    }

}
