package com.michibaum.gatewayservice.config

import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.io.HttpClientConnectionManager
import org.apache.hc.core5.util.Timeout
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate

@Configuration
class HttpClientConfiguration {

    @Bean
    fun httpClientConnectionManager(): HttpClientConnectionManager {
        return PoolingHttpClientConnectionManagerBuilder.create()
            .setMaxConnTotal(200)
            .setMaxConnPerRoute(50)
            .build()
    }

    @Bean
    fun clientHttpRequestFactory(connectionManager: HttpClientConnectionManager): ClientHttpRequestFactory {
        val requestConfig = RequestConfig.custom()
            .setConnectTimeout(Timeout.ofSeconds(5))
            .setResponseTimeout(Timeout.ofSeconds(10))
            .build()

        val httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build()

        return HttpComponentsClientHttpRequestFactory(httpClient)
    }

    @Bean
    fun restTemplate(clientHttpRequestFactory: ClientHttpRequestFactory): RestTemplate {
        return RestTemplate(clientHttpRequestFactory)
    }

    @Bean
    fun restClientBuilder(clientHttpRequestFactory: ClientHttpRequestFactory): RestClient.Builder {
        return RestClient.builder()
            .requestFactory(clientHttpRequestFactory)
    }
}
