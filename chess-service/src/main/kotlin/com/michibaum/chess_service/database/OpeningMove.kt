package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "opening_move")
data class OpeningMove(

    @Column(nullable = false)
    val move: String,

    @Column(nullable = false)
    val fen: String = "",

    @ManyToOne(targetEntity = OpeningMove::class, fetch = FetchType.LAZY, optional = true, cascade = [CascadeType.ALL])
    val parent: OpeningMove? = null,

    @Column(nullable = false)
    val deleted: Boolean = false,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)
