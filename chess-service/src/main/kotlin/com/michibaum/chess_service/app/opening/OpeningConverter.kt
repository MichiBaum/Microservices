package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.domain.Opening
import org.springframework.stereotype.Component
import java.util.*

@Component
class OpeningConverter {

    fun toDto(opening: Opening) =
        OpeningResponseDto(
            id = opening.id?.toString() ?: "",
            name = opening.name,
        )

    fun buildMoveHierarchy(moves: List<MoveHierarchyProjection>): OpeningMoveDto? {
        // Group evaluations by move ID
        val evaluationsByMoveId: Map<UUID, List<EvaluationDto>> = moves
            .filter { it.getEngineId() != null && it.getDepth() != null && it.getEvaluation() != null }
            .groupBy(
            keySelector = { it.getMoveId() },
            valueTransform = { move ->
                EvaluationDto(
                    engineId = move.getEngineId()!!.toString(),
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

        // Find the root of the tree (a move with no parentId)
        val rootMoveProjection = moves.find { it.getParentId() == null }
        return if (rootMoveProjection != null) {
            val rootId = rootMoveProjection.getMoveId()
            moveDtos.find { UUID.fromString(it.id) == rootId }
        } else {
            null // Return null if no root is found
        }
    }

}