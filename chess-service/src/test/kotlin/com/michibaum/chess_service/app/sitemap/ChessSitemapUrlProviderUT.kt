package com.michibaum.chess_service.app.sitemap

import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.database.OpeningRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class ChessSitemapUrlProviderUT {

    @Test
    fun `formats event and opening ids into relative chess sitemap paths`() {
        val eventRepository = mockk<EventRepository>()
        val openingRepository = mockk<OpeningRepository>()

        val eventId1 = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val eventId2 = UUID.fromString("22222222-2222-2222-2222-222222222222")
        val openingId1 = UUID.fromString("33333333-3333-3333-3333-333333333333")

        every { eventRepository.findAllIds() } returns listOf(eventId1, eventId2)
        every { openingRepository.findAllIds() } returns listOf(openingId1)

        val provider = ChessSitemapUrlProvider(eventRepository, openingRepository)
        val urls = provider.provideUrls()

        assertThat(urls).containsExactly(
            "/chess/events/11111111-1111-1111-1111-111111111111",
            "/chess/events/22222222-2222-2222-2222-222222222222",
            "/chess/openings/33333333-3333-3333-3333-333333333333"
        )
        verify(exactly = 1) { eventRepository.findAllIds() }
        verify(exactly = 1) { openingRepository.findAllIds() }
    }
}
