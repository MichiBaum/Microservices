package com.michibaum.authentication_starter

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.authentication_library.security.ServletDelegateAuthenticationManager
import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import com.michibaum.authentication_library.security.basic.BasicAuthenticationConverter
import com.michibaum.authentication_library.security.basic.BasicAuthenticationManager
import com.michibaum.authentication_library.security.basic.CredentialsValidator
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.authentication_library.security.jwt.JwtAuthenticationConverter
import com.michibaum.authentication_library.security.jwt.JwtAuthenticationManager
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import io.micrometer.observation.ObservationRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.AuthenticationConverter
import java.time.Duration

@AutoConfiguration
@EnableConfigurationProperties(value = [AdminServiceCredentials::class])
@EnableFeignClients(clients = [AuthenticationClient::class])
class AuthenticationAutoConfiguration {

    private val logger: Logger = LoggerFactory.getLogger(AuthenticationAutoConfiguration::class.java)

    @Bean
    @ConditionalOnMissingBean
    fun jwsValidator(authenticationClient: AuthenticationClient, circuitBreakerFactory: CircuitBreakerFactory<*, *>?): JwsValidator {
        logger.info("Creating JwsValidator in AuthenticationAutoConfiguration")
        return JwsValidator(authenticationClient, circuitBreakerFactory)
    }

    @Bean
    @ConditionalOnClass(Resilience4JCircuitBreakerFactory::class)
    @ConditionalOnMissingBean(name = ["authenticationPublicKeyCircuitBreakerCustomizer"])
    fun authenticationPublicKeyCircuitBreakerCustomizer(): Customizer<Resilience4JCircuitBreakerFactory> {
        logger.info("Creating authenticationPublicKeyCircuitBreakerCustomizer in AuthenticationAutoConfiguration")
        return Customizer { factory ->
            factory.configure({ builder ->
                builder
                    .timeLimiterConfig(
                        TimeLimiterConfig.custom()
                            .timeoutDuration(Duration.ofSeconds(5))
                            .build()
                    )
                    .circuitBreakerConfig(
                        CircuitBreakerConfig.custom()
                            .slidingWindowSize(10)
                            .failureRateThreshold(50.0f)
                            .waitDurationInOpenState(Duration.ofSeconds(30))
                            .permittedNumberOfCallsInHalfOpenState(3)
                            .slowCallRateThreshold(50.0f)
                            .slowCallDurationThreshold(Duration.ofSeconds(3))
                            .build()
                    )
            }, "authentication-service-public-key")
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun credentialsValidator(adminServiceCredentials: AdminServiceCredentials): CredentialsValidator{
        logger.info("Creating CredentialsValidator in AuthenticationAutoConfiguration")
        return CredentialsValidator { basic ->
            adminServiceCredentials.username == basic.getUsername() &&
                    adminServiceCredentials.password == basic.getPassword()
        }
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationManager::class)
    fun jwtAuthenticationManager(jwsValidator: JwsValidator, observationRegistry: ObservationRegistry): SpecificAuthenticationManager {
        logger.info("Creating JwtAuthenticationManager in AuthenticationAutoConfiguration")
        return JwtAuthenticationManager(jwsValidator, observationRegistry)
    }

    @Bean
    @ConditionalOnMissingBean(BasicAuthenticationManager::class)
    fun basicAuthenticationManager(credentialsValidator: CredentialsValidator, observationRegistry: ObservationRegistry): SpecificAuthenticationManager {
        logger.info("Creating BasicAuthenticationManager in AuthenticationAutoConfiguration")
        return BasicAuthenticationManager(credentialsValidator, observationRegistry)
    }

    @Bean
    @ConditionalOnMissingBean
    fun authenticationManager(specificAuthenticationManagers: List<SpecificAuthenticationManager>): AuthenticationManager {
        logger.info("Creating AuthenticationManager in AuthenticationAutoConfiguration")
        return ServletDelegateAuthenticationManager(specificAuthenticationManagers)
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationConverter::class)
    fun jwtAuthenticationConverter(): AuthenticationConverter {
        logger.info("Creating JwtAuthenticationConverter in AuthenticationAutoConfiguration")
        return JwtAuthenticationConverter()
    }

    @Bean
    @ConditionalOnMissingBean(BasicAuthenticationConverter::class)
    fun basicAuthenticationConverter(): AuthenticationConverter {
        logger.info("Creating BasicAuthenticationConverter in AuthenticationAutoConfiguration")
        return BasicAuthenticationConverter()
    }

    @Bean
    @ConditionalOnMissingBean
    fun authenticationFilter(authenticationManager: AuthenticationManager, authenticationConverters: List<AuthenticationConverter>): ServletAuthenticationFilter {
        logger.info("Creating AuthenticationFilter in AuthenticationAutoConfiguration")
        return ServletAuthenticationFilter(authenticationManager, authenticationConverters)
    }

}