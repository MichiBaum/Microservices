package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.app.game.GameConverter
import com.michibaum.chess_service.app.game.GameService
import com.michibaum.chess_service.app.person.PersonConverter
import io.mockk.*
import org.junit.jupiter.api.*
import java.time.LocalDate

class EventControllerUT {

    private val eventService: EventService = mockk()
    private val eventConverter: EventConverter = mockk()
    private val personConverter: PersonConverter = mockk()
    private val gameService: GameService = mockk()
    private val gameConverter: GameConverter = mockk()

    private val eventController = EventController(
        eventService,
        eventConverter,
        personConverter,
        gameService,
        gameConverter
    )

    @Test
    fun `getAllRecentAndUpcomingEvents calculates dates correctly`() {
        // GIVEN
        val now = LocalDate.now()
        val expectedRecent = now.minusMonths(1)
        val expectedUpcoming = now.plusMonths(2)

        every { eventService.findRecentAndUpcoming(expectedRecent, expectedUpcoming) } returns emptyList()

        // WHEN
        val result = eventController.getAllRecentAndUpcomingEvents()

        // THEN
        assertEquals(0, result.size)
        verify { eventService.findRecentAndUpcoming(expectedRecent, expectedUpcoming) }
    }
}

private fun assertEquals(expected: Int, actual: Int) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual)
}
