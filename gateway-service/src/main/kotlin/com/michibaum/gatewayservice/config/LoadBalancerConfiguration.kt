package com.michibaum.gatewayservice.config

import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

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
    fun restTemplate(): RestTemplate = RestTemplate()

    @Bean
    fun discoveryClientServiceInstanceListSupplier(
        context: ConfigurableApplicationContext,
        loadBalancerClientsProperties: LoadBalancerClientsProperties
    ): ServiceInstanceListSupplier {
        // Downstream services secure the plain /actuator/health endpoint (ADMIN_SERVICE authority only)
        // and only expose /actuator/health/liveness publicly. Configure the active health-check here,
        // in code, to target the already-public liveness probe for every service by default, instead
        // of listing each service id in application.yml.
        loadBalancerClientsProperties.healthCheck.path.putIfAbsent("default", "/actuator/health/liveness")

        return ServiceInstanceListSupplier.builder()
            .withBlockingDiscoveryClient()
            .withBlockingHealthChecks()
            .withCaching()
            .build(context)
    }

}
