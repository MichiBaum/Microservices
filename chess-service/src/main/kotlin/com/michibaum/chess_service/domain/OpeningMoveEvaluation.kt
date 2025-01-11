package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "opening_move_evaluation")
class OpeningMoveEvaluation(

    @ManyToOne(targetEntity = ChessEngine::class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "engine_id", nullable = false)
    val engine: ChessEngine,

    @ManyToOne(targetEntity = OpeningMove::class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opening_move_id", nullable = false)
    val openingMove: OpeningMove,

    @Column(nullable = false)
    val depth: Int,

    @Column(nullable = false)
    val evaluation: Float,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)