package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "game_move_evaluation")
class GameMoveEvaluation(

    @ManyToOne(targetEntity = ChessEngine::class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "engine_id", nullable = false)
    val engine: ChessEngine,

    @ManyToOne(targetEntity = GameMove::class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_move_id", nullable = false)
    val gameMove: GameMove,

    @Column(nullable = false)
    val depth: Int,

    @Column(nullable = false)
    val evaluation: Float,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)