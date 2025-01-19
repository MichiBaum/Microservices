package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.domain.Opening
import com.michibaum.chess_service.domain.OpeningMove
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class OpeningService(
    private val openingRepository: OpeningRepository,
    private val openingMoveRepository: OpeningMoveRepository
) {

    fun getOpeningById(openingId: UUID): Opening? {
        return openingRepository.findById(openingId).getOrNull()
    }

    fun createOpening(dto: OpeningDto): Opening {
        val lastMove: OpeningMove = openingMoveRepository.findById(dto.moveId)
            .orElseThrow { IllegalArgumentException("Invalid lastMoveId: ${dto.moveId}") }

        val opening = Opening(
            name = dto.name,
            lastMove = lastMove
        )

        return openingRepository.save(opening)
    }

    fun updateOpening(id: UUID, dto: OpeningDto): Opening {
        val opening = openingRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Opening not found with id: $id") }

        val lastMove: OpeningMove = openingMoveRepository.findById(dto.moveId)
            .orElseThrow { IllegalArgumentException("Invalid lastMoveId: ${dto.moveId}") }

        // Update fields
        val updatedOpening = opening.copy(
            name = dto.name,
            lastMove = lastMove
        )

        return openingRepository.save(updatedOpening)
    }

    fun getAll(): List<Opening> {
        return openingRepository.findAll()
    }

    fun findMoveHierarchy(lastMove: OpeningMove): List<MoveHierarchyProjection> {
        return openingMoveRepository.findMoveHirarchyBefore(lastMove.id!!)
    }

    fun findMoveChildren(move: OpeningMove): List<MoveHierarchyProjection> {
        return openingMoveRepository.findMoveChildren(move.id!!)
    }

    fun findMoveBy(uuid: UUID): OpeningMove? {
        return openingMoveRepository.findById(uuid).getOrNull()
    }

    fun findMoveByParent(id: UUID?): List<OpeningMove> {
        return openingMoveRepository.findAllByParentId(id)
    }

    fun openingsByMoves(moves: List<OpeningMove>): List<Opening> {
        return openingRepository.findByLastMoveIn(moves)
    }

    fun createMove(dto: WriteOpeningMoveDto, parentMove: OpeningMove): OpeningMove {
        val newMove = OpeningMove(
            move = dto.move,
            parent = parentMove
        )
        return openingMoveRepository.save(newMove)
    }

}