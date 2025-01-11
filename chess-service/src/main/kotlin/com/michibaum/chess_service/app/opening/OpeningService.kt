package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.domain.Opening
import com.michibaum.chess_service.domain.OpeningMove
import org.springframework.stereotype.Service
import java.util.*

@Service
class OpeningService(
    private val openingRepository: OpeningRepository,
    private val openingMoveRepository: OpeningMoveRepository
) {

    fun getOpeningById(openingId: UUID): Opening {
        return openingRepository.findById(openingId)
            .orElseThrow { IllegalArgumentException("Opening not found with id: $openingId") }
    }

    fun createOpening(dto: OpeningDto): Opening {
        val lastMove: OpeningMove = openingMoveRepository.findById(dto.lastMoveId)
            .orElseThrow { IllegalArgumentException("Invalid lastMoveId: ${dto.lastMoveId}") }

        val opening = Opening(
            name = dto.name,
            lastMove = lastMove
        )

        return openingRepository.save(opening)
    }

    fun updateOpening(id: UUID, dto: OpeningDto): Opening {
        val opening = openingRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Opening not found with id: $id") }

        val lastMove: OpeningMove = openingMoveRepository.findById(dto.lastMoveId)
            .orElseThrow { IllegalArgumentException("Invalid lastMoveId: ${dto.lastMoveId}") }

        // Update fields
        val updatedOpening = opening.copy(
            name = dto.name,
            lastMove = lastMove
        )

        return openingRepository.save(updatedOpening)
    }
}