package com.michibaum.chess.domain

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
data class Game(
    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    val chessPlatform: ChessPlatform,

    @Column(nullable = false)
    val platformId: String,

    @Column(nullable = false)
    val pgn: String,

    @Enumerated(EnumType.STRING)
    val gameType: GameType,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ACCOUNT_GAME_MAPPING", joinColumns = [JoinColumn(name = "game_id")],
        inverseJoinColumns = [JoinColumn(name = "account_id")])
    val accounts: Set<Account> = emptySet(),

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER)
    val players: Set<Player> = emptySet()

)
