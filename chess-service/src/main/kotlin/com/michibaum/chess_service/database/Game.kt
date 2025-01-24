package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="game")
class Game(
    @Enumerated(EnumType.STRING)
    val chessPlatform: ChessPlatform,

    @Column(nullable = false)
    val platformId: String,

    @Lob
    @Column(nullable = false, length=1024)
    val pgn: String,

    @Column(nullable = true)
    var openingName: String? = "",

    @Enumerated(EnumType.STRING)
    val gameType: GameType,

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER, targetEntity = Player::class, cascade = [CascadeType.ALL])
    val players: Set<Player>,

    @ManyToOne(targetEntity = Event::class, fetch = FetchType.LAZY, optional = true, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name="event_id", nullable=true)
    val event: Event? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    ){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
