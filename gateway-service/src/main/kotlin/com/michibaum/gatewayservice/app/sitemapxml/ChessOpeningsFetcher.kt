package com.michibaum.gatewayservice.app.sitemapxml

import com.michibaum.gatewayservice.config.feign.ChessClient
import org.springframework.stereotype.Component

@Component
class ChessOpeningsFetcher (
    private val chessClient: ChessClient
) : DataLocationFetcher {

    override fun supports(dataLocation: DataLocation): Boolean {
        return dataLocation == DataLocation.CHESS_OPENINGS
    }

    override fun fetch(): List<String> {
        return chessClient.openings().map { it.id }
    }
}
