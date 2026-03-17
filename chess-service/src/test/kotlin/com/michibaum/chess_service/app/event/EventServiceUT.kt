package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.database.GameRepository
import com.michibaum.chess_service.app.eventcategory.EventCategoryService
import com.michibaum.chess_service.database.PersonRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class EventServiceUT {

    private val eventRepository: EventRepository = mockk()
    private val eventCategoryService: EventCategoryService = mockk()
    private val personRepository: PersonRepository = mockk()
    private val gameRepository: GameRepository = mockk()

    private val eventService = EventService(
        eventRepository,
        eventCategoryService,
        personRepository,
        gameRepository
    )

    @Test
    fun `findRecentAndUpcoming calls repository with correct dates`() {
        // GIVEN
        val recent = LocalDate.now().minusMonths(1)
        val upcoming = LocalDate.now().plusMonths(2)
        every { eventRepository.findByDateToGreaterThanAndDateFromLessThan(recent, upcoming) } returns emptyList()

        // WHEN
        val result = eventService.findRecentAndUpcoming(recent, upcoming)

        // THEN
        assertEquals(0, result.size)
        verify { eventRepository.findByDateToGreaterThanAndDateFromLessThan(recent, upcoming) }
    }
}
