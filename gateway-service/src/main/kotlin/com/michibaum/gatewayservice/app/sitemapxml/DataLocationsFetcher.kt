package com.michibaum.gatewayservice.app.sitemapxml

import com.michibaum.gatewayservice.config.feign.ChessClient
import org.springframework.stereotype.Component

@Component
class DataLocationsFetcher(
    private val chessClient: ChessClient
) {

    fun fetch(dataLocation: DataLocation): List<String>{
        return when (dataLocation) {
            DataLocation.CHESS_EVENTS -> chessClient.events().map { it.id }
            DataLocation.CHESS_OPENINGS -> chessClient.openings().map { it.id }
        }
    }

}