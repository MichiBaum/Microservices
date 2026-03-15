package com.michibaum.chess_service.app.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.EventRepository
import com.michibaum.chess_service.database.PersonRepository
import com.michibaum.chess_service.domain.EventProvider
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
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class EventControllerUpdateIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should update event when data is valid`(){
        // GIVEN
        val event = EventProvider.event(title = "Old Title")
        val savedEvent = eventRepository.save(event)

        val updateDto = WriteEventDto(
            title = "Updated Title",
            location = "Updated Location",
            dateFrom = "2026-06-01",
            dateTo = "2026-06-10",
            platform = ChessPlatform.LICHESS
        )

        // WHEN
        mockMvc.perform(
            put("/api/events/${savedEvent.id}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.id").value(savedEvent.id.toString()),
            jsonPath("$.title").value("Updated Title"),
            jsonPath("$.location").value("Updated Location")
        )
    }

    @Test
    fun `update returns notFound if event id not found`(){
        // GIVEN
        val uuid = UUID.randomUUID()
        val updateDto = WriteEventDto(
            title = "Updated Title",
            location = "Updated Location",
            dateFrom = "2026-06-01",
            dateTo = "2026-06-10",
            platform = ChessPlatform.LICHESS
        )

        // WHEN
        mockMvc.perform(
            put("/api/events/${uuid}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isNotFound
        )
    }

    @Test
    fun `update returns badRequest if event id malformated`(){
        // GIVEN
        val uuid = "abc"
        val updateDto = WriteEventDto(
            title = "Updated Title",
            location = "Updated Location",
            dateFrom = "2026-06-01",
            dateTo = "2026-06-10",
            platform = ChessPlatform.LICHESS
        )

        // WHEN
        mockMvc.perform(
            put("/api/events/${uuid}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )
    }

    @Test
    fun `update should return bad request when title is empty`(){
        // GIVEN
        val event = EventProvider.event(title = "Old Title")
        val savedEvent = eventRepository.save(event)

        val updateDto = WriteEventDto(
            title = "",
            location = "Updated Location",
            dateFrom = "2026-06-01",
            dateTo = "2026-06-10",
            platform = ChessPlatform.LICHESS
        )

        // WHEN
        mockMvc.perform(
            put("/api/events/${savedEvent.id}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )
    }

    @Test
    fun `should update event by adding a participant`() {
        // GIVEN
        val person1 = personRepository.save(PersonProvider.person())
        val person2 = personRepository.save(PersonProvider.person())
        val event = EventProvider.event(participants = mutableSetOf(person1))
        val savedEvent = eventRepository.save(event)

        val updateDto = WriteEventDto(
            title = savedEvent.title,
            location = savedEvent.location,
            dateFrom = savedEvent.dateFrom.toString(),
            dateTo = savedEvent.dateTo.toString(),
            platform = savedEvent.platform,
            participantsIds = listOf(person1.id.toString(), person2.id.toString())
        )

        // WHEN
        mockMvc.perform(
            put("/api/events/${savedEvent.id}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isOk)

        // THEN
        mockMvc.perform(
            get("/api/events/${savedEvent.id}/participants")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.length()").value(2),
            jsonPath("$[*].id").value(hasItems(person1.id.toString(), person2.id.toString()))
        )
    }

    @Test
    fun `should update event by adding multiple participant`() {
        // GIVEN
        val person1 = personRepository.save(PersonProvider.person())
        val person2 = personRepository.save(PersonProvider.person())
        val person3 = personRepository.save(PersonProvider.person())
        val event = EventProvider.event(participants = mutableSetOf(person1))
        val savedEvent = eventRepository.save(event)

        val updateDto = WriteEventDto(
            title = savedEvent.title,
            location = savedEvent.location,
            dateFrom = savedEvent.dateFrom.toString(),
            dateTo = savedEvent.dateTo.toString(),
            platform = savedEvent.platform,
            participantsIds = listOf(person1.id.toString(), person2.id.toString(), person3.id.toString())
        )

        // WHEN
        mockMvc.perform(
            put("/api/events/${savedEvent.id}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        // THEN
        mockMvc.perform(
            get("/api/events/${savedEvent.id}/participants")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.length()").value(3),
            jsonPath("$[*].id").value(hasItems(person1.id.toString(), person2.id.toString()))
        )
    }

    @Test
    fun `should update event by removing a participant`() {
        // GIVEN
        val person1 = personRepository.save(PersonProvider.person())
        val person2 = personRepository.save(PersonProvider.person())
        val event = EventProvider.event(participants = mutableSetOf(person1, person2))
        val savedEvent = eventRepository.save(event)

        val updateDto = WriteEventDto(
            title = savedEvent.title,
            location = savedEvent.location,
            dateFrom = savedEvent.dateFrom.toString(),
            dateTo = savedEvent.dateTo.toString(),
            platform = savedEvent.platform,
            participantsIds = listOf(person1.id.toString())
        )

        // WHEN
        mockMvc.perform(
            put("/api/events/${savedEvent.id}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        // THEN
        mockMvc.perform(
            get("/api/events/${savedEvent.id}/participants")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.length()").value(1),
            jsonPath("$[0].id").value(person1.id.toString())
        )
    }

    @Test
    fun `should update event by removing multiple participant`() {
        // GIVEN
        val person1 = personRepository.save(PersonProvider.person())
        val person2 = personRepository.save(PersonProvider.person())
        val person3 = personRepository.save(PersonProvider.person())
        val event = EventProvider.event(participants = mutableSetOf(person1, person2, person3))
        val savedEvent = eventRepository.save(event)

        val updateDto = WriteEventDto(
            title = savedEvent.title,
            location = savedEvent.location,
            dateFrom = savedEvent.dateFrom.toString(),
            dateTo = savedEvent.dateTo.toString(),
            platform = savedEvent.platform,
            participantsIds = listOf(person1.id.toString())
        )

        // WHEN
        mockMvc.perform(
            put("/api/events/${savedEvent.id}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        // THEN
        mockMvc.perform(
            get("/api/events/${savedEvent.id}/participants")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.length()").value(1),
            jsonPath("$[0].id").value(person1.id.toString())
        )
    }

    @Test
    fun `should update event by changing participants`() {
        // GIVEN
        val person1 = personRepository.save(PersonProvider.person())
        val person2 = personRepository.save(PersonProvider.person())
        val person3 = personRepository.save(PersonProvider.person())
        val event = EventProvider.event(participants = mutableSetOf(person1, person2))
        val savedEvent = eventRepository.save(event)

        val updateDto = WriteEventDto(
            title = savedEvent.title,
            location = savedEvent.location,
            dateFrom = savedEvent.dateFrom.toString(),
            dateTo = savedEvent.dateTo.toString(),
            platform = savedEvent.platform,
            participantsIds = listOf(person2.id.toString(), person3.id.toString())
        )

        // WHEN
        mockMvc.perform(
            put("/api/events/${savedEvent.id}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        // THEN
        mockMvc.perform(
            get("/api/events/${savedEvent.id}/participants")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.length()").value(2),
            jsonPath("$[*].id").value(hasItems(person2.id.toString(), person3.id.toString()))
        )
    }

}
