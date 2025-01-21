package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.OpeningMove
import com.michibaum.chess_service.database.OpeningMoveRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class MoveHirarchyQueryIT {

    @Autowired
    lateinit var openingMoveRepository: OpeningMoveRepository

    @Test
    fun test() {
        // GIVEN
        val rootMove = OpeningMove(move = "e4", parent = null)
        val move1 = OpeningMove(move = "e5", parent = rootMove)
        val move2 = OpeningMove(move = "Nf3", parent = move1)
        val move3 = OpeningMove(move = "Nc6", parent = move2)
        val saved = openingMoveRepository.save(move3)

        // WHEN
        val result = openingMoveRepository.findMoveHirarchyBefore(saved.id!!)

        // THEN
        assertEquals(4, result.size)
    }

}