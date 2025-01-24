package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "game_move")
class GameMove(

    @ManyToOne(targetEntity = Game::class, fetch = FetchType.LAZY, optional = false)
    val game: Game,

    @Column(nullable = false)
    val isWhite: Boolean,

    @Column(nullable = false)
    val move: String,

    @Column(nullable = false)
    val moveNumber: Int,

    @OneToMany(mappedBy = "gameMove", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val moveEvaluations: Set<GameMoveEvaluation> = emptySet(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)