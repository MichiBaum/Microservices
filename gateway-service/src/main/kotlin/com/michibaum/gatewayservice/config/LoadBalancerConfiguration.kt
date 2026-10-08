package com.michibaum.gatewayservice.config

import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean

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

    @Bean
    fun discoveryClientServiceInstanceListSupplier(
        context: ConfigurableApplicationContext
    ): ServiceInstanceListSupplier =
        ServiceInstanceListSupplier.builder()
            .withDiscoveryClient()
            .withHealthChecks()
            .withCaching()
            .build(context)

}
