package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.database.Game
import com.michibaum.chess_service.database.GameRepository
import com.michibaum.chess_service.database.PersonRepository
import com.michibaum.chess_service.domain.EventProvider
import com.michibaum.chess_service.domain.GameProvider
import com.michibaum.chess_service.domain.PersonProvider
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import kotlin.test.assertTrue

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class EventControllerDeleteIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var gameRepository: GameRepository
    
    @Autowired
    lateinit var personRepository: PersonRepository

    @Test
    fun `should delete event when id is valid`(){
        // GIVEN
        val event = EventProvider.event(title = "Title to be deleted")
        val savedEvent = eventRepository.save(event)
        val id = savedEvent.id!!

        // WHEN
        mockMvc.perform(
            delete("/api/events/$id")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
        ).andExpect(
            status().isNoContent
        )

        // THEN
        assertFalse(eventRepository.existsById(id))
    }

    @Test
    fun `delete returns notFound if event id not found`(){
        // GIVEN
        val uuid = UUID.randomUUID()

        // WHEN
        mockMvc.perform(
            delete("/api/events/$uuid")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
        ).andExpect(
            status().isNotFound
        )
    }

    @Test
    fun `delete returns badRequest if event id malformed`(){
        // GIVEN
        val id = "abc"

        // WHEN
        mockMvc.perform(
            delete("/api/events/$id")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
        ).andExpect(
            status().isBadRequest
        )
    }

    @Test
    fun `should delete event and nullify it in games`(){
        // GIVEN
        val event = EventProvider.event(title = "Title to be deleted")
        // No eventRepository.save(event) here, let cascade handle it or save it before and then find it again to be attached

        val game = GameProvider.game(event = event)
        val savedGame = gameRepository.save(game)
        val savedEvent = savedGame.event!!
        val id = savedEvent.id!!

        // WHEN
        mockMvc.perform(
            delete("/api/events/$id")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
        ).andExpect(
            status().isNoContent
        )

        // THEN
        assertFalse(eventRepository.existsById(id))
        val updatedGame = gameRepository.findById(savedGame.id!!).get()
        assertNull(updatedGame.event)
    }

    @Test
    fun `should delete event with players`(){
        // GIVEN
        val person1 = personRepository.save(PersonProvider.person())
        val person2 = personRepository.save(PersonProvider.person())
        val event = EventProvider.event(title = "Title to be deleted", participants = mutableSetOf(person1, person2))
        val savedEvent = eventRepository.save(event)

        // WHEN
        mockMvc.perform(
            delete("/api/events/${savedEvent.id}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
        ).andExpect(
            status().isNoContent
        )

        // THEN
        assertFalse(eventRepository.existsById(savedEvent.id!!))
        assertTrue(personRepository.existsById(person1.id!!))
        assertTrue(personRepository.existsById(person2.id!!))
    }

}
