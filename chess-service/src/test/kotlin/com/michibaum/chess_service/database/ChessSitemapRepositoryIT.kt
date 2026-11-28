package com.michibaum.chess_service.database

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.domain.EventProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestcontainersConfiguration
class ChessSitemapRepositoryIT {

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var openingRepository: OpeningRepository

    @Test
    fun `findAllIds returns event and active opening UUIDs without loading deleted openings`() {
        val savedEvent = eventRepository.save(EventProvider.event(title = "Zürich Chess Open"))
        val activeOpening = openingRepository.save(
            Opening(name = "Ruy Lopez", lastMove = OpeningMove(move = "Bb5"), deleted = false)
        )
        val deletedOpening = openingRepository.save(
            Opening(name = "Deleted Gambit", lastMove = OpeningMove(move = "f4"), deleted = true)
        )

        val eventIds = eventRepository.findAllIds()
        val openingIds = openingRepository.findAllIds()

        assertThat(eventIds).contains(savedEvent.id)
        assertThat(openingIds).contains(activeOpening.id)
        assertThat(openingIds).doesNotContain(deletedOpening.id)
    }
}
