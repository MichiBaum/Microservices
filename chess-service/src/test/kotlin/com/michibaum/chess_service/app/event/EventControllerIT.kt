package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.PersonRepository
import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.domain.EventProvider
import com.michibaum.chess_service.domain.PersonProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class EventControllerIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var personRepository: PersonRepository


    @Test
    fun `return all events`(){
        // GIVEN
        val event = EventProvider.event()
        val savedEvent = eventRepository.save(event)

        // WHEN
        mockMvc.perform(
            get("/api/events")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.[0].id").exists(),
            jsonPath("$.[0].title").exists(),
        )


        // THEN


    }

    @Test
    fun `return specific event`(){
        // GIVEN
        val event = EventProvider.event()
        val savedEvent = eventRepository.save(event)

        // WHEN
        mockMvc.perform(
            get("/api/events/${savedEvent.id}")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.id").exists(),
            jsonPath("$.title").exists(),
        )

        // THEN


    }

    @Test
    fun `event return notFound if event id not found`(){
        // GIVEN
        val event = EventProvider.event()
        val savedEvent = eventRepository.save(event)
        var uuid: UUID?
        do {
            uuid = UUID.randomUUID()
        } while (savedEvent.id == uuid)

        // WHEN
        mockMvc.perform(
            get("/api/events/${uuid}")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isNotFound
        )

        // THEN


    }

    @Test
    fun `event return badRequest if event id malformated`(){
        // GIVEN
        val event = EventProvider.event()
        val savedEvent = eventRepository.save(event)
        val uuid = "abc"

        // WHEN
        mockMvc.perform(
            get("/api/events/${uuid}")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )

        // THEN

    }

    @Test
    fun `return events participants`(){
        // GIVEN
        val person = PersonProvider.person()
        val savedPerson = personRepository.save(person)
        val event = EventProvider.event(mutableSetOf(savedPerson))
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
        val event = EventProvider.event(mutableSetOf(savedPerson))
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
        val event = EventProvider.event(mutableSetOf(savedPerson))
        val savedEvent = eventRepository.save(event)
        val uuid = "abc"

        // WHEN
        mockMvc.perform(
            get("/api/events/${uuid}/participants")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )

        // THEN


    }


}