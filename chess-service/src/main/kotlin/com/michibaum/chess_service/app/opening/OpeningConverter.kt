package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.database.ChessEngine
import com.michibaum.chess_service.database.MoveHierarchyProjection
import com.michibaum.chess_service.database.Opening
import com.michibaum.chess_service.database.OpeningMove
import io.micrometer.observation.annotation.Observed
import com.michibaum.chess_service.database.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class OpeningConverter {

    fun toDto(opening: Opening) =
        OpeningResponseDto(
            id = opening.id?.toString() ?: "",
            name = opening.name,
            moveId = opening.lastMove.id?.toString() ?: ""
        )

    fun toDto(opening: PopularOpening) =
        PopularOpeningResponseDto(
            id = opening.opening.id?.toString() ?: "",
            name = opening.opening.name,
            moveId = opening.opening.lastMove.id?.toString() ?: "",
            ranking = opening.ranking
        )

    fun toDto(evaluation: OpeningMoveEvaluation): OpeningMoveEvaluationDto{
        return OpeningMoveEvaluationDto(
            id = evaluation.id!!.toString(),
            engineId = evaluation.engine.id!!.toString(),
            depth = evaluation.depth,
            evaluation = evaluation.evaluation
        )
    }

    @Observed
    fun buildMoveHierarchy(moves: List<MoveHierarchyProjection>, engines: List<ChessEngine>, firstMoveId: UUID? = null, ): OpeningMoveDto? {
        // Group evaluations by move ID
        val evaluationsByMoveId: Map<UUID, List<EvaluationDto>> = moves
            .filter { it.getEngineId() != null && it.getDepth() != null && it.getEvaluation() != null }
            .groupBy(
            keySelector = { it.getMoveId() },
            valueTransform = { move ->
                val chessEngine = engines.find { engine -> engine.id == move.getEngineId() }
                EvaluationDto(
                    id = move.getEvaluationId()!!,
                    engineId = move.getEngineId()!!.toString(),
                    engineName = chessEngine?.name ?: "",
                    engineVersion = chessEngine?.version ?: "",
                    depth = move.getDepth()!!,
                    evaluation = move.getEvaluation()!!
                )
            }
        )

        // Map to collect child relationships (parentId -> list of child projections)
        val childrenMap = mutableMapOf<UUID, MutableList<OpeningMoveDto>>()

        // Build a DTO for each move and prepare the children relationship map
        val moveDtoMap = mutableMapOf<UUID, OpeningMoveDto>() // Map of moveId to DTO

        val moveDtos = moves.map { move ->
            val moveId = move.getMoveId()

            // Reuse an existing DTO if it already exists
            val dto = moveDtoMap.getOrPut(moveId) {
                OpeningMoveDto(
                    id = moveId.toString(),
                    move = move.getMove(),
                    fen = move.getFen(),
                    openingName = move.getOpeningName(),
                    openingId = move.getOpeningId()?.toString(),
                    nextMoves = listOf(),
                    evaluations = evaluationsByMoveId[moveId] ?: listOf()
                )
            }

            move.getParentId()?.let { parentId ->
                val childList = childrenMap.getOrPut(parentId) { mutableListOf() }
                if (!childList.contains(dto)) { // Prevent duplicates in children
                    childList.add(dto)
                }
            }

            dto
        }

        // Assign children to the respective parent DTOs
        moveDtos.forEach { dto ->
            val dtoIdAsUuid = UUID.fromString(dto.id)
            dto.nextMoves = childrenMap[dtoIdAsUuid] ?: listOf()
        }

        if(firstMoveId != null) {
            return moveDtos.find { UUID.fromString(it.id) == firstMoveId }
        }

        // Find the root of the tree
        val rootMoveProjection = moves.find { it.getParentId() == null }
        if (rootMoveProjection != null) {
            val rootId = rootMoveProjection.getMoveId()
            return moveDtos.find { UUID.fromString(it.id) == rootId }
        }

        throw NoSuchElementException("No root found (with: $firstMoveId) for moves: $moves")
    }

    fun convert(move: OpeningMove): SimpleOpeningMoveDto {
        return SimpleOpeningMoveDto(
            id = move.id?.toString() ?: "",
            move = move.move,
            fen = move.fen,
            parentMoveId = move.parent?.id?.toString() ?: ""
        )
    }

}