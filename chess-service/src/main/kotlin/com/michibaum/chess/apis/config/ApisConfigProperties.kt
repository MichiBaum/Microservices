package com.michibaum.chess.apis.config

import com.michibaum.chess.domain.ChessPlatform
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.reactive.function.client.WebClient

data class ApisConfigProperties(
    var name: String = "",
    var baseUrl: String = ""
){

    val webClient: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .codecs { configurer ->
            configurer.defaultCodecs()
            .maxInMemorySize(10000000)
        }
        .build()

}

@ConfigurationProperties(value = "chess-apis")
data class ChessConfigProperties(
    var properties: Map<ChessPlatform, ApisConfigProperties> = emptyMap()
) {
    fun getWebClient(apis: ChessPlatform): WebClient {
        return properties[apis]!!.webClient
    }
}

