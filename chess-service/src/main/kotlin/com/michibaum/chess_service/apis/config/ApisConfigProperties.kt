package com.michibaum.chess_service.apis.config

import com.michibaum.chess_service.database.ChessPlatform
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

data class ApisConfigProperties(
    var name: String = "",
    var baseUrl: String = ""
){

    fun createRestClient(restClientBuilder: RestClient.Builder): RestClient {
        return restClientBuilder.baseUrl(baseUrl)
            .defaultHeader("User-Agent", "chess-statistic-application/0.1; +https://michibaum.ch")
            .build()
    }

}

@Configuration
@ConfigurationProperties(value = "chess-apis")
data class ChessConfigProperties(
    var properties: Map<ChessPlatform, ApisConfigProperties> = emptyMap()
) {
    fun createRestClient(restClientBuilder: RestClient.Builder, apis: ChessPlatform): RestClient {
        return properties[apis]?.createRestClient(restClientBuilder) ?: throw NoSuchElementException("No WebClient configured for $apis")
    }
}

