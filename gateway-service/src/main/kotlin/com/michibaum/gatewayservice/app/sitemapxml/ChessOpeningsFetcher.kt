package com.michibaum.gatewayservice.app.sitemapxml

import com.michibaum.cache.ProjectCacheable
import com.michibaum.gatewayservice.config.feign.ChessClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ChessOpeningsFetcher (
    private val chessClient: ChessClient
) : DataLocationFetcher {

    override fun supports(dataLocation: DataLocation): Boolean {
        return dataLocation == DataLocation.CHESS_OPENINGS
    }

    @ProjectCacheable(cacheNames = ["chess-opening-fetcher"], ttl = 1, ttlUnit = TimeUnit.DAYS)
    override fun fetch(): List<String> {
        return chessClient.openings().map { it.id }
    }
}
