package com.michibaum.chess_service.domain

import com.michibaum.chess_service.apis.dtos.PieceColor
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
class Player(
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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
)