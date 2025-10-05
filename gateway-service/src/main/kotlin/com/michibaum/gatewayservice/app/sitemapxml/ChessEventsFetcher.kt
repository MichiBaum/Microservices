package com.michibaum.gatewayservice.app.sitemapxml

import com.michibaum.cache.ProjectCacheable
import com.michibaum.gatewayservice.config.feign.ChessClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ChessEventsFetcher (
    private val chessClient: ChessClient
) : DataLocationFetcher {

    override fun supports(dataLocation: DataLocation): Boolean {
        return dataLocation == DataLocation.CHESS_EVENTS
    }

    @ProjectCacheable(cacheNames = ["chess-events-fetcher"], ttl = 1, ttlUnit = TimeUnit.DAYS)
    override fun fetch(): List<String> {
        return chessClient.events().map { it.id }
    }
}
