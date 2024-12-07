package com.michibaum.chess_service.app.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.chess_service.app.person.PersonRepository
import com.michibaum.chess_service.domain.EventProvider
import com.michibaum.chess_service.domain.PersonProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@AutoConfigureWebTestClient
@SpringBootTest
class EventControllerIT {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

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
        webTestClient.get()
            .uri("/api/events")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                {it.expectStatus().isOk},
                {it.expectBody()}
            )

        // THEN


    }

    @Test
    fun `return specific event`(){
        // GIVEN
        val event = EventProvider.event()
        val savedEvent = eventRepository.save(event)

        // WHEN
        webTestClient.get()
            .uri("/api/events/${savedEvent.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                {it.expectStatus().isOk},
                {it.expectBody()}
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
        } while (savedEvent.id != uuid)

        // WHEN
        webTestClient.get()
            .uri("/api/events/${uuid}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                {it.expectStatus().isNotFound},
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
        webTestClient.get()
            .uri("/api/events/${uuid}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                {it.expectStatus().isBadRequest},
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
        webTestClient.get()
            .uri("/api/events/${savedEvent.id}/participants")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                {it.expectStatus().isOk},
                {it.expectBody()}
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
        } while (savedEvent.id != uuid)

        // WHEN
        webTestClient.get()
            .uri("/api/events/${uuid}/participants")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                {it.expectStatus().isNotFound}
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
        webTestClient.get()
            .uri("/api/events/${uuid}/participants")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                {it.expectStatus().isBadRequest}
            )

        // THEN


    }


}