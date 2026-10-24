package com.michibaum.gatewayservice.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.loadbalancer.DefaultRequest
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.client.SimpleClientHttpRequestFactory
import reactor.core.publisher.Flux

class LoadBalancerConfigurationUT {

    @Test
    fun `restTemplate is configured with SimpleClientHttpRequestFactory`() {
        val configuration = LoadBalancerConfiguration()
        val restTemplate = configuration.restTemplate()

        assertNotNull(restTemplate)
        assertInstanceOf(SimpleClientHttpRequestFactory::class.java, restTemplate.requestFactory)
    }

    @Test
    fun `discoveryClientServiceInstanceListSupplier configures default liveness path`() {
        val configuration = LoadBalancerConfiguration()
        val context = mock(ConfigurableApplicationContext::class.java)
        val environment = mock(org.springframework.core.env.ConfigurableEnvironment::class.java)
        `when`(environment.getProperty(org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory.PROPERTY_NAME))
            .thenReturn("test-service")
        `when`(context.environment).thenReturn(environment)
        `when`(context.getBean(org.springframework.cloud.client.discovery.DiscoveryClient::class.java))
            .thenReturn(mock(org.springframework.cloud.client.discovery.DiscoveryClient::class.java))
        `when`(context.getBean(org.springframework.web.client.RestTemplate::class.java))
            .thenReturn(mock(org.springframework.web.client.RestTemplate::class.java))
        `when`(context.getBean(org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties::class.java))
            .thenReturn(LoadBalancerClientsProperties())
        `when`(context.getBean(org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory::class.java))
            .thenReturn(mock(org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory::class.java))
        @Suppress("UNCHECKED_CAST")
        val cacheManagerProvider = mock(org.springframework.beans.factory.ObjectProvider::class.java) as org.springframework.beans.factory.ObjectProvider<org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager>
        `when`(context.getBeanProvider(org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager::class.java))
            .thenReturn(cacheManagerProvider)
        val loadBalancerClientsProperties = LoadBalancerClientsProperties()

        configuration.discoveryClientServiceInstanceListSupplier(context, loadBalancerClientsProperties)

        assertEquals("/actuator/health/liveness", loadBalancerClientsProperties.healthCheck.path["default"])

        val configuredProperties = LoadBalancerClientsProperties().apply {
            healthCheck.path["default"] = "/configured/liveness"
        }
        configuration.discoveryClientServiceInstanceListSupplier(context, configuredProperties)

        assertEquals("/configured/liveness", configuredProperties.healthCheck.path["default"])
    }

    @Test
    fun `DiscoveryFallbackServiceInstanceListSupplier falls back to discovery supplier when delegate is empty`() {
        val instance = mock(ServiceInstance::class.java)
        val healthCheckSupplier = mock(ServiceInstanceListSupplier::class.java)
        val discoverySupplier = mock(ServiceInstanceListSupplier::class.java)

        `when`(healthCheckSupplier.get()).thenReturn(Flux.just(emptyList()))
        `when`(discoverySupplier.get()).thenReturn(Flux.just(listOf(instance)))

        val request = DefaultRequest<Any>()
        `when`(healthCheckSupplier.get(request)).thenReturn(Flux.just(emptyList()))
        `when`(discoverySupplier.get(request)).thenReturn(Flux.just(listOf(instance)))

        val fallbackSupplier = DiscoveryFallbackServiceInstanceListSupplier(healthCheckSupplier, discoverySupplier)

        val resultNoArg = fallbackSupplier.get().blockFirst()
        assertEquals(listOf(instance), resultNoArg)

        val resultWithReq = fallbackSupplier.get(request).blockFirst()
        assertEquals(listOf(instance), resultWithReq)
    }

    @Test
    fun `DiscoveryFallbackServiceInstanceListSupplier returns health check instances when non-empty`() {
        val instance = mock(ServiceInstance::class.java)
        val healthCheckSupplier = mock(ServiceInstanceListSupplier::class.java)
        val discoverySupplier = mock(ServiceInstanceListSupplier::class.java)

        `when`(healthCheckSupplier.get()).thenReturn(Flux.just(listOf(instance)))

        val request = DefaultRequest<Any>()
        `when`(healthCheckSupplier.get(request)).thenReturn(Flux.just(listOf(instance)))

        val fallbackSupplier = DiscoveryFallbackServiceInstanceListSupplier(healthCheckSupplier, discoverySupplier)

        val resultNoArg = fallbackSupplier.get().blockFirst()
        assertEquals(listOf(instance), resultNoArg)

        val resultWithReq = fallbackSupplier.get(request).blockFirst()
        assertEquals(listOf(instance), resultWithReq)
    }
}
