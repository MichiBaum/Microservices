package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.EventCategory
import com.michibaum.chess_service.database.EventCategoryRepository
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

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class EventControllerSearchIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var eventCategoryRepository: EventCategoryRepository

    @Test
    fun `should search events by title`(){
        // GIVEN
        val event = EventProvider.event(title = "Searchable Title")
        eventRepository.save(event)

        // WHEN
        mockMvc.perform(
            get("/api/events/search")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .param("title", "Searchable Title")
                .param("pageNumber", "0")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.[0].title").value("Searchable Title")
        )
    }

    @Test
    fun `should search events by location`(){
        // GIVEN
        val event = EventProvider.event(location = "Searchable Location")
        eventRepository.save(event)

        // WHEN
        mockMvc.perform(
            get("/api/events/search")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .param("location", "Searchable Location")
                .param("pageNumber", "0")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.[0].location").value("Searchable Location")
        )
    }

    @Test
    fun `should search events by category`(){
        // GIVEN
        val category = EventCategory(name = "Searchable Category", description = "Description")
        val savedCategory = eventCategoryRepository.save(category)
        val event = EventProvider.event(title = "Category Event", categories = setOf(savedCategory))
        eventRepository.save(event)

        // WHEN
        mockMvc.perform(
            get("/api/events/search")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .param("category", "Searchable Category")
                .param("pageNumber", "0")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.[0].title").value("Category Event")
        )
    }

}
