package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.*
import org.hamcrest.Matchers.containsInAnyOrder
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

    @Autowired
    lateinit var chessEngineRepository: ChessEngineRepository

    @Autowired
    lateinit var openingMoveEvaluationRepository: OpeningMoveEvaluationRepository

    @Test
    fun `should return move hierarchy for a valid opening`() {
        // GIVEN
        val move1 = OpeningMove(move = "e4", parent = null)
        val move2 = OpeningMove(move = "e5", parent = move1)
        val move3 = OpeningMove(move = "Nf3", parent = move2)
        val move4 = OpeningMove(move = "Nc6", parent = move3)
        val opening = Opening(name = "Italian Game", lastMove = move4)
        openingRepository.save(opening)

        // WHEN
        mockMvc.perform(get("/api/openings/${opening.id}/moves"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.move").value(move1.move))
            .andExpect(jsonPath("$.nextMoves[0].move").value(move2.move))
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].move").value(move3.move))
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].nextMoves[0].move").value(move4.move))
    }

    @Test
    fun `should return one move hierarchy for a valid opening`() {
        // GIVEN
        val move = OpeningMove(move = "e4", parent = null)
        val opening = Opening(name = "Kings pawn opening", lastMove = move)
        openingRepository.save(opening)

        // WHEN
        mockMvc.perform(get("/api/openings/${opening.id}/moves"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.move").value(move.move))
    }

    @Test
    fun `should return move hierarchy with evaluation for a valid opening`() {
        // GIVEN
        val stockfish = chessEngineRepository.save(ChessEngine(name = "Stockfish", version = "15.1"))
        val lc0 = chessEngineRepository.save(ChessEngine(name = "lc0", version = "0.31"))

        val move1 = OpeningMove(move = "e4", parent = null)
        val move2 = OpeningMove(move = "e5", parent = move1)
        val move3 = OpeningMove(move = "Nf3", parent = move2)
        val move4 = OpeningMove(move = "Nc6", parent = move3)
        val opening = Opening(name = "Italian Game", lastMove = move4)
        openingRepository.save(opening)

        val eval1 = openingMoveEvaluationRepository.save(OpeningMoveEvaluation(stockfish, move3, 30, "0.25"))
        val eval2 = openingMoveEvaluationRepository.save(OpeningMoveEvaluation(lc0, move3, 35, "0.3"))
        val eval3 = openingMoveEvaluationRepository.save(OpeningMoveEvaluation(lc0, move4, 10, "1"))

        // WHEN
        mockMvc.perform(get("/api/openings/${opening.id}/moves"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.move").value(move1.move)) // move 1
            .andExpect(jsonPath("$.nextMoves[0].move").value(move2.move)) // move 2
            .andExpect(jsonPath("$.nextMoves[0].evaluations.length()").value(0)) // move 2
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].move").value(move3.move)) // move 3
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].evaluations.length()").value(2)) // move 3
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].evaluations.[*].depth").value(containsInAnyOrder(eval1.depth, eval2.depth))) // move 3
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].evaluations.[*].evaluation").value(containsInAnyOrder(eval1.evaluation, eval2.evaluation))) // move 3
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].nextMoves[0].move").value(move4.move)) // move 4
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].nextMoves[0].evaluations.[0].depth").value(eval3.depth)) // move 4
            .andExpect(jsonPath("$.nextMoves[0].nextMoves[0].nextMoves[0].evaluations.[0].evaluation").value(eval3.evaluation)) // move 4
    }

    @Test
    fun `should return 404 for an invalid opening`() {
        // WHEN
        mockMvc.perform(get("/api/openings/${UUID.randomUUID()}/moves"))
            .andExpect(status().isNotFound)
    }


}