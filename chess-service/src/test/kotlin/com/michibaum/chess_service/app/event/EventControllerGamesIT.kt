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
class EventControllerGamesIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var eventRepository: EventRepository

    @Test
    fun `should return games for an event`(){
        // GIVEN
        val event = EventProvider.event()
        val savedEvent = eventRepository.save(event)

        // WHEN
        mockMvc.perform(
            get("/api/events/${savedEvent.id}/games")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$").isArray
        )
    }

    @Test
    fun `games returns notFound if event id not found`(){
        // GIVEN
        val uuid = UUID.randomUUID()

        // WHEN
        mockMvc.perform(
            get("/api/events/${uuid}/games")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isNotFound
        )
    }

    @Test
    fun `games returns badRequest if event id malformated`(){
        // GIVEN
        val uuid = "abc"

        // WHEN
        mockMvc.perform(
            get("/api/events/${uuid}/games")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )
    }

}
