package com.michibaum.lifemanagementbackend

import com.michibaum.lifemanagementbackend.config.PropertyLogger
import com.michibaum.lifemanagementbackend.publicendpoint.PublicEndpoint
import com.michibaum.lifemanagementbackend.publicendpoint.PublicEndpointDetails
import com.michibaum.lifemanagementbackend.publicendpoint.PublicEndpointSearcher
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
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
        val publicEndpointSearcher = PublicEndpointSearcher("com.michibaum.lifemanagementbackend.controller")
        val restControllers: List<Class<*>> = publicEndpointSearcher.allRestController

        return restControllers
            .map { clazz: Class<*> -> publicEndpointSearcher.getMethodsAnnotatedWith(clazz, PublicEndpoint::class.java)}
            .flatten()
            .map(publicEndpointSearcher::getRequestMappingValue)
            .flatten()
    }
}
