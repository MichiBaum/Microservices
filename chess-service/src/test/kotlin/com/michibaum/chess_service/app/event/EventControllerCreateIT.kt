package com.michibaum.chess_service.app.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.database.PersonRepository
import com.michibaum.chess_service.domain.PersonProvider
import org.hamcrest.Matchers.hasItems
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class EventControllerCreateIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should create event when data is valid`(){
        // GIVEN
        val eventDto = WriteEventDto(
            title = "New Event",
            location = "London",
            dateFrom = "2026-05-01",
            dateTo = "2026-05-10",
            platform = ChessPlatform.FIDE
        )

        // WHEN
        mockMvc.perform(
            put("/api/events")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isCreated,
            jsonPath("$.id").exists(),
            jsonPath("$.title").value("New Event"),
            jsonPath("$.location").value("London"),
            jsonPath("$.dateFrom").value("2026-05-01"),
            jsonPath("$.dateTo").value("2026-05-10")
        )

        // THEN
    }

    @Test
    fun `should return bad request when title is empty`(){
        // GIVEN
        val eventDto = WriteEventDto(
            title = "",
            location = "London",
            dateFrom = "2026-05-01",
            dateTo = "2026-05-10",
            platform = ChessPlatform.FIDE
        )

        // WHEN
        mockMvc.perform(
            put("/api/events")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )
    }

    @Test
    fun `should return bad request when date format is invalid`(){
        // GIVEN
        val eventDto = WriteEventDto(
            title = "Invalid Date Event",
            location = "London",
            dateFrom = "01-05-2026", // Invalid format
            dateTo = "2026-05-10",
            platform = ChessPlatform.FIDE
        )

        // WHEN
        mockMvc.perform(
            put("/api/events")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )
    }

    @Test
    fun `should save internal comment when user is admin`(){
        // GIVEN
        val eventDto = WriteEventDto(
            title = "Admin Event",
            location = "London",
            dateFrom = "2026-05-01",
            dateTo = "2026-05-10",
            platform = ChessPlatform.FIDE,
            internalComment = "Secret comment"
        )

        // WHEN
        mockMvc.perform(
            put("/api/events")
                .with(user("admin").authorities(SimpleGrantedAuthority("CHESS_SERVICE"), SimpleGrantedAuthority("CHESS_SERVICE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isCreated,
            jsonPath("$.internalComment").value("Secret comment")
        )

        // THEN
    }

    @Test
    fun `should not save internal comment when user is not admin`(){
        // GIVEN
        val eventDto = WriteEventDto(
            title = "Non-Admin Event",
            location = "London",
            dateFrom = "2026-05-01",
            dateTo = "2026-05-10",
            platform = ChessPlatform.FIDE,
            internalComment = "Secret comment"
        )

        // WHEN
        mockMvc.perform(
            put("/api/events")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isCreated,
            jsonPath("$.internalComment").value("")
        )

        // THEN
    }

    @Test
    fun `should create event with participants`() {
        // GIVEN
        val person1 = personRepository.save(PersonProvider.person())
        val person2 = personRepository.save(PersonProvider.person())

        val eventDto = WriteEventDto(
            title = "Event with participants",
            location = "Zurich",
            dateFrom = "2026-06-01",
            dateTo = "2026-06-10",
            platform = ChessPlatform.FIDE,
            participantsIds = listOf(person1.id.toString(), person2.id.toString())
        )

        // WHEN
        val result = mockMvc.perform(
            put("/api/events")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated)
            .andReturn()

        val createdEventId = objectMapper.readTree(result.response.contentAsString).get("id").asText()

        // THEN
        mockMvc.perform(
            get("/api/events/$createdEventId/participants")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.length()").value(2),
            jsonPath("$[*].id").value(hasItems(person1.id.toString(), person2.id.toString()))
        )
    }

}
