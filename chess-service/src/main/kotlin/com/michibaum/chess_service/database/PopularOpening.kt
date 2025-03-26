package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "popular_opening")
data class PopularOpening(

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "opening_id")
    val opening: Opening,

    @Column(nullable = false)
    val ranking: Int = 0,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)
