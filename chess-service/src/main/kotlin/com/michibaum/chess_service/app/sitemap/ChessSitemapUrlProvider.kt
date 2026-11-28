package com.michibaum.chess_service.app.sitemap

import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.database.OpeningRepository
import com.michibaum.gateway_discovery.sitemap.SitemapUrlProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChessSitemapUrlProvider(
    private val eventRepository: EventRepository,
    private val openingRepository: OpeningRepository
) : SitemapUrlProvider {

    @Transactional(readOnly = true)
    override fun provideUrls(): List<String> {
        val eventUrls = eventRepository.findAllIds().map { "/chess/events/$it" }
        val openingUrls = openingRepository.findAllIds().map { "/chess/openings/$it" }
        return eventUrls + openingUrls
    }
}
