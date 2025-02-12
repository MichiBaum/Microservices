package com.michibaum.chess_service.apis.config

import com.michibaum.chess_service.database.ChessPlatform
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.client.RestClient

/**
 * Configuration properties for API clients.
 *
 * This class holds the configuration properties for a specific API,
 * including the name and base URL. It also provides a pre-configured
 * WebClient instance with default settings.
 *
 * @property name The name of the API.
 * @property baseUrl The base URL of the API.
 * @property webClient The configured WebClient instance for this API.
 */
data class ApisConfigProperties(
    var name: String = "",
    var baseUrl: String = ""
){

    /**
     * A pre-configured instance of `WebClient` for interacting with external APIs
     * specific to the chess statistic application.
     *
     * This instance is built with a base URL and a custom User-Agent header.
     * Additionally, it configures the maximum in-memory size for codecs to 10MB.
     */
    val webClient: RestClient = RestClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("User-Agent", "chess-statistic-application/0.1; +http://www.michibaum.ch")
        .build()

}

@ConfigurationProperties(value = "chess-apis")
data class ChessConfigProperties(
    var properties: Map<ChessPlatform, ApisConfigProperties> = emptyMap()
) {
    fun getWebClient(apis: ChessPlatform): RestClient {
        return properties[apis]?.webClient ?: throw NoSuchElementException("No WebClient configured for $apis")
    }
}

