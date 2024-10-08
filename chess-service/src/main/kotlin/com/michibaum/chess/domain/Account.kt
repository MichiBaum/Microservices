package com.michibaum.chess.domain

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
data class Account(
    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val accId: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val username: String,

    @Column(nullable = false)
    val url: String,

    @Enumerated(EnumType.STRING)
    val platform: ChessPlatform,

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    val createdAt: Date,

    @ManyToOne
    @JoinColumn(name="person_id", nullable=true)
    val person: Person?,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ACCOUNT_GAME_MAPPING", joinColumns = [JoinColumn(name = "account_id")],
        inverseJoinColumns = [JoinColumn(name = "game_id")])
    val games: Set<Game> = emptySet()

) {

}