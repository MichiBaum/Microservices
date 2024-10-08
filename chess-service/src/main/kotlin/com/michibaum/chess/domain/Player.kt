package com.michibaum.chess.domain

import com.michibaum.chess.apis.dtos.PieceColor
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
data class Player(
    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val platformId: String,

    @Column(nullable = false)
    val username: String,

    @Column(nullable = false)
    val rating: Long,

    @Enumerated(EnumType.STRING)
    val pieceColor: PieceColor,

    @ManyToOne
    @JoinColumn(name="game_id", nullable=false)
    val game: Game,
)