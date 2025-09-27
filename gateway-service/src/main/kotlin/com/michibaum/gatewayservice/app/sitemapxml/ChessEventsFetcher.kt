package com.michibaum.gatewayservice.app.sitemapxml

import com.michibaum.cache.ProjectCacheable
import com.michibaum.gatewayservice.config.feign.ChessClient
import org.springframework.stereotype.Component

@Component
class ChessEventsFetcher (
    private val chessClient: ChessClient
) : DataLocationFetcher {

    override fun supports(dataLocation: DataLocation): Boolean {
        return dataLocation == DataLocation.CHESS_EVENTS
    }

    @ProjectCacheable(cacheNames = ["chess-events-fetcher"])
    override fun fetch(): List<String> {
        return chessClient.events().map { it.id }
    }
}
