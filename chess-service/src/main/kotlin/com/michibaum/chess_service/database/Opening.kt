package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "opening")
data class Opening(

    @Column(nullable = false)
    val name: String,

    @ManyToOne(targetEntity = OpeningMove::class, fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.ALL])
    val lastMove: OpeningMove,

    @Column(nullable = false)
    val deleted: Boolean = false,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)