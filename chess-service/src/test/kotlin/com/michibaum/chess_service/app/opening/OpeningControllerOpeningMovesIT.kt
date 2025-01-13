package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.domain.Opening
import com.michibaum.chess_service.domain.OpeningMove
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.UUID

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class OpeningControllerOpeningMovesIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var openingRepository: OpeningRepository

    @Test
    fun `should return move hierarchy for a valid opening`() {
        val rootMove = OpeningMove(move = "e4", parent = null)
        val move1 = OpeningMove(move = "e5", parent = rootMove)
        val move2 = OpeningMove(move = "Nf3", parent = move1)
        val move3 = OpeningMove(move = "Nc6", parent = move2)
        val opening = Opening(name = "Italian Game", lastMove = move3)
        openingRepository.save(opening)

        mockMvc.perform(get("/api/openings/${opening.id}/moves"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.move").value(rootMove.move))
            .andExpect(jsonPath("$.nextMoves[0].move").value(move1.move))
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].move").value(move2.move))
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].nextMoves[0].move").value(move3.move))
    }

    @Test
    fun `should return one move hierarchy for a valid opening`() {
        val move = OpeningMove(move = "e4", parent = null)
        val opening = Opening(name = "Kings pawn opening", lastMove = move)
        openingRepository.save(opening)

        mockMvc.perform(get("/api/openings/${opening.id}/moves"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.move").value(move.move))
    }

    @Test
    fun `should return 404 for an invalid opening`() {
        mockMvc.perform(get("/api/openings/${UUID.randomUUID()}/moves"))
            .andExpect(status().isNotFound)
    }

}