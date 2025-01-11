package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.domain.Opening
import com.michibaum.chess_service.domain.OpeningMove
import org.springframework.stereotype.Component
import java.util.*

@Component
class OpeningConverter {

    fun toDto(opening: Opening) =
        OpeningResponseDto(
            id = opening.id?.toString() ?: "",
            name = opening.name,
            lastMoveId = opening.lastMove.id?.toString() ?: ""
        )

    fun buildMoveHierarchy(move: OpeningMove): OpeningMoveDto {
        // Traverse up to find the root move
        var currentMove: OpeningMove? = move
        val moves = mutableListOf<OpeningMove>()

        // Collect all moves in a list starting from the input move to the root
        while (currentMove != null) {
            moves.add(currentMove)
            currentMove = currentMove.parent
        }

        // Reverse the list to start from the root move
        val reversedMoves = moves.reversed()

        // Build hierarchical OpeningMoveDto objects
        var rootDto: OpeningMoveDto? = null
        var currentDto: OpeningMoveDto? = null

        for (openingMove in reversedMoves) {
            val newDto = OpeningMoveDto(
                id = openingMove.id?.toString() ?: UUID.randomUUID().toString(),
                move = openingMove.move,
                nextMoves = listOf()
            )

            if (rootDto == null) {
                rootDto = newDto // Set the root move DTO to the first move
            } else {
                currentDto!!.nextMoves = listOf(newDto) // Add the new move to the current hierarchical chain
            }

            currentDto = newDto // Update the current DTO to the new one
        }

        // Return the root DTO, representing the full hierarchy
        return rootDto!!
    }

}