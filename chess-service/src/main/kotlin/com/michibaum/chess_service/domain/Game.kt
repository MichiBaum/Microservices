package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.util.*

@Entity
class Game(
    @Enumerated(EnumType.STRING)
    val chessPlatform: ChessPlatform,

    @Column(nullable = false)
    val platformId: String,

    @Lob
    @Column(nullable = false, length=1024)
    val pgn: String,

    @Enumerated(EnumType.STRING)
    val gameType: GameType,

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Account::class)
    @JoinTable(name = "ACCOUNT_GAME_MAPPING", joinColumns = [JoinColumn(name = "game_id")], inverseJoinColumns = [JoinColumn(name = "account_id")])
    val accounts: Set<Account>,

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER, targetEntity = Player::class, cascade = [CascadeType.ALL])
    val players: Set<Player>,

    @ManyToOne(targetEntity = Event::class, fetch = FetchType.LAZY, optional = true, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name="event_id", nullable=true)
    val event: Event? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

)
