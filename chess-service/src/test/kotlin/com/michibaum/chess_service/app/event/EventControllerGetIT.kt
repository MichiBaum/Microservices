package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.domain.EventProvider
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
class EventControllerGetIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var eventRepository: EventRepository

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
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )

        // THEN
    }

}
