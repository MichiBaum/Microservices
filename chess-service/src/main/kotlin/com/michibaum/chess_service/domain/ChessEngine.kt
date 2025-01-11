package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "chess_engine")
data class ChessEngine(

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val version: String,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)