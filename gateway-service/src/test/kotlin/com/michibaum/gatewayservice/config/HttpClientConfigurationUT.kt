package com.michibaum.gatewayservice.config

import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory

class HttpClientConfigurationUT {

    @Test
    fun `httpClientConnectionManager creates valid connection manager`() {
        val configuration = HttpClientConfiguration()
        val connectionManager = configuration.httpClientConnectionManager()

        assertNotNull(connectionManager)
    }

    @Test
    fun `clientHttpRequestFactory creates HttpComponentsClientHttpRequestFactory`() {
        val configuration = HttpClientConfiguration()
        val connectionManager = configuration.httpClientConnectionManager()
        val factory = configuration.clientHttpRequestFactory(connectionManager)

        assertNotNull(factory)
        assertInstanceOf(HttpComponentsClientHttpRequestFactory::class.java, factory)
    }

    @Test
    fun `restTemplate creates valid RestTemplate with request factory`() {
        val configuration = HttpClientConfiguration()
        val connectionManager = configuration.httpClientConnectionManager()
        val factory = configuration.clientHttpRequestFactory(connectionManager)
        val restTemplate = configuration.restTemplate(factory)

        assertNotNull(restTemplate)
        assertInstanceOf(HttpComponentsClientHttpRequestFactory::class.java, restTemplate.requestFactory)
    }

    @Test
    fun `restClientBuilder creates valid RestClient Builder`() {
        val configuration = HttpClientConfiguration()
        val connectionManager = configuration.httpClientConnectionManager()
        val factory = configuration.clientHttpRequestFactory(connectionManager)
        val restClientBuilder = configuration.restClientBuilder(factory)

        assertNotNull(restClientBuilder)
    }
}
