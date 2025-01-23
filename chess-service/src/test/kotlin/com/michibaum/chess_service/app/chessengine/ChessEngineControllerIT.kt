package com.michibaum.chess_service.app.chessengine

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.ChessEngine
import com.michibaum.chess_service.database.ChessEngineRepository
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class ChessEngineControllerIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var chessEngineRepository: ChessEngineRepository

    @Test
    fun `should return empty list when there are no chess engines`() {
        mockMvc.perform(get("/api/engines"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(0)) // Expect 0 items in the response
    }

    @Test
    fun `should return a single chess engine when one exists`() {
        val chessEngine = chessEngineRepository.save(
            ChessEngine(name = "Stockfish", version = "15.1")
        )

        mockMvc.perform(get("/api/engines"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1)) // Expect exactly 1 item
            .andExpect(jsonPath("$[0].id").value(chessEngine.id.toString()))
            .andExpect(jsonPath("$[0].name").value("Stockfish"))
            .andExpect(jsonPath("$[0].version").value("15.1"))
    }

    @Test
    fun `should return multiple chess engines when multiple exist`() {
        val stockfish = ChessEngine(name = "Stockfish", version = "15.1")
        val komodo = ChessEngine(name = "Komodo", version = "14.2")
        val leela = ChessEngine(name = "Leela Chess Zero", version = "0.30")
        chessEngineRepository.saveAll(listOf(stockfish, komodo, leela))

        mockMvc.perform(get("/api/engines"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(3)) // Expect exactly 3 items
            .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("Stockfish", "Komodo", "Leela Chess Zero")))
            .andExpect(jsonPath("$[*].version").value(containsInAnyOrder("15.1", "14.2", "0.30")))
    }



}