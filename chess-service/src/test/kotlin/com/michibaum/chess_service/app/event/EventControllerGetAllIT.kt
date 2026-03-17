package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.domain.EventProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class EventControllerGetAllIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var eventRepository: EventRepository

    @Test
    fun `returns all events`(){
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

}
