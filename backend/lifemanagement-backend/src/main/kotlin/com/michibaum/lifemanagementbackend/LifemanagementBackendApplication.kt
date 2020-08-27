package com.michibaum.lifemanagementbackend

import com.michibaum.lifemanagementbackend.core.config.PropertyLogger
import com.michibaum.lifemanagementbackend.core.publicendpoint.PublicEndpoint
import com.michibaum.lifemanagementbackend.core.publicendpoint.PublicEndpointDetails
import com.michibaum.lifemanagementbackend.core.publicendpoint.PublicEndpointSearcher
import com.michibaum.lifemanagementbackend.core.security.SecurityConstants
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import javax.annotation.PostConstruct
import kotlin.random.Random


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
    fun argon2PasswordEncoder() = Argon2PasswordEncoder(24, 40, 2, 1 shl 15, 4)

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

    @Bean
    fun startingSecret() = createSecret()

    @PostConstruct
    fun createJWTSecret(){
        SecurityConstants.SECRET = createSecret()
    }

    private fun createSecret(): String {
        val sb = StringBuffer()
        while (sb.length < 32) {
            sb.append(String.format("%08x", Random.nextInt()))
        }
        return sb.toString().substring(0, 32)
    }

}
