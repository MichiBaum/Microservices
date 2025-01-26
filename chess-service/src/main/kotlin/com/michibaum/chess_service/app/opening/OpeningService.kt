package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.database.*
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class OpeningService(
    private val openingRepository: OpeningRepository,
    private val openingMoveRepository: OpeningMoveRepository,
    private val popularOpeningRepository: PopularOpeningRepository
) {

    fun getPopularOpenings(): List<PopularOpening> {
        return popularOpeningRepository.findAllByOrderByRankingAsc()
    }

    fun getOpeningByIdEagerLastMove(openingId: UUID): Opening? {
        return openingRepository.findByIdEagerLastMove(openingId)
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

    fun getAllEagerLastMove(deleted: Boolean): List<Opening> {
        return openingRepository.findAllByDeletedFalseEagerLastMove(deleted)
    }

    fun findMoveHierarchy(lastMove: OpeningMove): List<MoveHierarchyProjection> {
        return openingMoveRepository.findMoveHirarchyBefore(lastMove.id!!)
    }

    fun findMoveChildren(move: OpeningMove, maxDepth: Int): List<MoveHierarchyProjection> {
        return openingMoveRepository.findMoveChildren(move.id!!, maxDepth)
    }

    fun findMoveBy(uuid: UUID): OpeningMove? {
        return openingMoveRepository.findById(uuid).getOrNull()
    }

    fun findOpeningByParentMoveAndDeleted(id: UUID?, deleted: Boolean): List<Opening> {
        return openingRepository.findOpeningByParentMoveAndDeleted(id, deleted)
    }

    fun createMove(dto: WriteOpeningMoveDto, parentMove: OpeningMove): OpeningMove {
        val newMove = OpeningMove(
            move = dto.move,
            fen = dto.fen,
            parent = parentMove
        )
        return openingMoveRepository.save(newMove)
    }

    fun updateMove(dto: WriteOpeningMoveDto, move: OpeningMove): OpeningMove {
        val newMove = OpeningMove(
            id = move.id,
            move = dto.move,
            fen = dto.fen,
            parent = move.parent,
            moveEvaluations = move.moveEvaluations
        )
        return openingMoveRepository.save(newMove)
    }

    fun deleteOpening(opening: Opening): Opening {
        val toSave = opening.copy( deleted = true )
        return openingRepository.save(toSave)
    }

    fun deleteMove(move: OpeningMove): OpeningMove {
        val toSave = move.copy( deleted = true )
        return openingMoveRepository.save(toSave)
    }

}