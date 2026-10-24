package com.michibaum.gatewayservice.config

import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.time.Duration

/**
 * Custom LoadBalancer configuration enabling active health-checking of cached service instances,
 * on top of the default caching behaviour.
 *
 * This class is intentionally NOT annotated with `@Configuration` so that it is only picked up
 * as a LoadBalancer client configuration (via `@LoadBalancerClients(defaultConfiguration = ...)`
 * on [com.michibaum.gatewayservice.GatewayServiceApplication]) instead of being scanned into the
 * main application context.
 */
class LoadBalancerConfiguration {

    /**
     * `withBlockingHealthChecks()` needs a plain (non-load-balanced) `RestTemplate` bean to ping
     * cached instances. This LoadBalancer client child context does not inherit such a bean from
     * the main application context (the gateway itself has none, since it uses Feign/WebClient),
     * so it must be provided here explicitly.
     */
    @Bean
    fun restTemplate(): RestTemplate {
        val requestFactory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(Duration.ofSeconds(2))
            setReadTimeout(Duration.ofSeconds(2))
        }
        return RestTemplate(requestFactory)
    }

    @Bean
    fun discoveryClientServiceInstanceListSupplier(
        context: ConfigurableApplicationContext,
        loadBalancerClientsProperties: LoadBalancerClientsProperties
    ): ServiceInstanceListSupplier {
        // Keep this fallback for deployments that omit the shared default from configuration.
        loadBalancerClientsProperties.healthCheck.path.putIfAbsent("default", "/actuator/health/liveness")

        return ServiceInstanceListSupplier.builder()
            .withBlockingDiscoveryClient()
            .withBlockingHealthChecks()
            .withCaching()
            .build(context)
    }

}
