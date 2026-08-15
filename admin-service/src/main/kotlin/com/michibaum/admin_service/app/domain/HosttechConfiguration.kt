package com.michibaum.admin_service.app.domain

import com.michibaum.admin_service.app.domain.api.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient

@Configuration
class HosttechConfiguration {

    @Bean
    fun hosttechApiClient(objectMapper: ObjectMapper): ApiClient {
        val client = ApiClient(HttpClient.newBuilder(), objectMapper, "https://api.ns1.hosttech.eu")
        client.setRequestInterceptor { builder ->
            HosttechAuthContext.getToken()?.let { token ->
                builder.header("Authorization", "Bearer $token")
            }
        }
        return client
    }

    @Bean
    fun zonesApi(hosttechApiClient: ApiClient): ZonesApi =
        ZonesApi(hosttechApiClient)

    @Bean
    fun recordsApi(hosttechApiClient: ApiClient): RecordsApi =
        RecordsApi(hosttechApiClient)

    @Bean
    fun tokensApi(hosttechApiClient: ApiClient): TokensApi =
        TokensApi(hosttechApiClient)

    @Bean
    fun toolsApi(hosttechApiClient: ApiClient): ToolsApi =
        ToolsApi(hosttechApiClient)

    @Bean
    fun usersApi(hosttechApiClient: ApiClient): UsersApi =
        UsersApi(hosttechApiClient)

    @Bean
    fun nameserversetsApi(hosttechApiClient: ApiClient): NameserversetsApi =
        NameserversetsApi(hosttechApiClient)

}
