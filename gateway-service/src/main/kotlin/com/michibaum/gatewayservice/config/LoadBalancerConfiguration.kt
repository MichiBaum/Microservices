package com.michibaum.gatewayservice.config

import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties
import org.springframework.cloud.client.loadbalancer.Request
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import reactor.core.publisher.Flux
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
            setConnectTimeout(Duration.ofSeconds(5))
            setReadTimeout(Duration.ofSeconds(5))
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

        val discoverySupplier = ServiceInstanceListSupplier.builder()
            .withBlockingDiscoveryClient()
            .build(context)

        val healthCheckSupplier = ServiceInstanceListSupplier.builder()
            .withBlockingDiscoveryClient()
            .withBlockingHealthChecks()
            .build(context)

        val fallbackSupplier = DiscoveryFallbackServiceInstanceListSupplier(healthCheckSupplier, discoverySupplier)

        return ServiceInstanceListSupplier.builder()
            .withBase(fallbackSupplier)
            .withCaching()
            .build(context)
    }

}

class DiscoveryFallbackServiceInstanceListSupplier(
    delegate: ServiceInstanceListSupplier,
    private val discoverySupplier: ServiceInstanceListSupplier
) : DelegatingServiceInstanceListSupplier(delegate) {

    override fun get(): Flux<List<ServiceInstance>> {
        return delegate.get()
            .onErrorResume { discoverySupplier.get() }
            .flatMap { instances ->
                if (instances.isEmpty()) {
                    discoverySupplier.get()
                } else {
                    Flux.just(instances)
                }
            }
    }

    override fun get(request: Request<*>): Flux<List<ServiceInstance>> {
        return delegate.get(request)
            .onErrorResume { discoverySupplier.get(request) }
            .flatMap { instances ->
                if (instances.isEmpty()) {
                    discoverySupplier.get(request)
                } else {
                    Flux.just(instances)
                }
            }
    }
}
