package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "opening_move")
class OpeningMove(

    @Column(nullable = false)
    val move: String,

    @ManyToOne(targetEntity = OpeningMove::class, fetch = FetchType.LAZY, optional = true, cascade = [CascadeType.ALL])
    val parent: OpeningMove? = null,

    @OneToMany(mappedBy = "openingMove", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val moveEvaluations: Set<OpeningMoveEvaluation> = emptySet(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)