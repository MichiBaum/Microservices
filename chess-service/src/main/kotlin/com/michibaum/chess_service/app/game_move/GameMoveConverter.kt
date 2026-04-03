package com.michibaum.chess_service.app.game_move

import com.michibaum.chess_service.database.GameMove
import com.michibaum.chess_service.database.GameMoveEvaluation
import org.springframework.stereotype.Component

@Component
class GameMoveConverter {

    fun convert(gameMove: GameMove): GameMoveResponseDto {
        return GameMoveResponseDto(
            id = gameMove.id ?: throw IllegalStateException("GameMove ID must not be null"),
            moveNumber = gameMove.moveNumber,
            isWhite = gameMove.isWhite,
            move = gameMove.move,
            evaluations = gameMove.moveEvaluations.map { convertEvaluation(it) }
        )
    }

    private fun convertEvaluation(evaluation: GameMoveEvaluation): GameMoveEvaluationResponseDto {
        return GameMoveEvaluationResponseDto(
            id = evaluation.id ?: throw IllegalStateException("GameMoveEvaluation ID must not be null"),
            engineName = evaluation.engine.name,
            engineVersion = evaluation.engine.version,
            depth = evaluation.depth,
            evaluation = evaluation.evaluation
        )
    }

}