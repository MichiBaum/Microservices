package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.domain.Opening
import com.michibaum.chess_service.domain.OpeningMove
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class OpeningControllerGetAllMovesIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var openingRepository: OpeningRepository

    @Test
    fun `should return a list of openings when openings exist`() {
        openingRepository.save(Opening(name = "Sicilian Defense", OpeningMove(move = "e4")))
        openingRepository.save(Opening(name = "French Defense", OpeningMove(move = "e3")))

        mockMvc.perform(get("/api/openings"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("Sicilian Defense", "French Defense")))
    }

    @Test
    fun `should return an empty list when no openings exist`() {
        mockMvc.perform(get("/api/openings"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(0))  // No openings in response
    }

}