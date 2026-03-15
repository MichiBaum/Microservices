package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.database.PersonRepository
import com.michibaum.chess_service.domain.EventProvider
import com.michibaum.chess_service.domain.PersonProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class EventControllerParticipantsIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    @Test
    fun `return events participants`(){
        // GIVEN
        val person = PersonProvider.person()
        val savedPerson = personRepository.save(person)
        val event = EventProvider.event(participants = mutableSetOf(savedPerson))
        val savedEvent = eventRepository.save(event)

        // WHEN
        mockMvc.perform(
            get("/api/events/${savedEvent.id}/participants")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.[0].id").exists(),
            jsonPath("$.[0].firstname").exists(),
            jsonPath("$.[0].lastname").exists(),
        )

        // THEN
    }

    @Test
    fun `participants returns notFound if event id not found`(){
        // GIVEN
        val person = PersonProvider.person()
        val savedPerson = personRepository.save(person)
        val event = EventProvider.event(participants = mutableSetOf(savedPerson))
        val savedEvent = eventRepository.save(event)
        var uuid: UUID?
        do {
            uuid = UUID.randomUUID()
        } while (savedEvent.id == uuid)

        // WHEN
        mockMvc.perform(
            get("/api/events/${uuid}/participants")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isNotFound
        )

        // THEN
    }

    @Test
    fun `participants returns badRequest if event id malformated`(){
        // GIVEN
        val person = PersonProvider.person()
        val savedPerson = personRepository.save(person)
        val event = EventProvider.event(participants = mutableSetOf(savedPerson))
        val savedEvent = eventRepository.save(event)
        val uuid = "abc"

        // WHEN
        mockMvc.perform(
            get("/api/events/${uuid}/participants")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )

        // THEN
    }

}
