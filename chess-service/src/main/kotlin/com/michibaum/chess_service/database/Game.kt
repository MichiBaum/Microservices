package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

/**
 * Represents a chess game in the system.
 * A game can originate from an online platform, be played over-the-board (OTB), or be manually imported.
 */
@Entity
@Table(name="game")
@NamedEntityGraphs(
    value = [
        NamedEntityGraph(
            name = "with-event",
            attributeNodes = [NamedAttributeNode("event")]
        ),
        NamedEntityGraph(
            name = "with-players",
            attributeNodes = [
                NamedAttributeNode("whitePlayer"),
                NamedAttributeNode("blackPlayer")
            ]
        )
    ]
)
class Game(
    /**
     * The source type of the game (Online, OTB, or Imported).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var sourceType: SourceType,

    /**
     * The platform where the game was played (optional if not an online game).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    var platform: ChessPlatform? = null,

    /**
     * The unique identifier of the game on the external platform.
     */
    @Column(nullable = true)
    var externalGameId: String? = null,

    /**
     * Raw PGN content of the game.
     */
    @Lob
    @Column(nullable = false, length=2048)
    var pgn: String,

    @Column(nullable = true)
    var openingName: String? = null,

    /**
     * The result of the game.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var gameResult: GameResult,

    /**
     * How the game ended (e.g., checkmate, resignation, timeout).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    var termination: TerminationType? = null,

    /**
     * When the game was played.
     */
    @Column(nullable = false)
    var playedAt: java.time.LocalDateTime,

    /**
     * Descriptive time control string (e.g., "300+5").
     */
    @Column(nullable = true)
    var timeControl: String? = null,

    /**
     * Category of the time control (Bullet, Blitz, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    var timeControlCategory: TimeControlCategory? = null,

    /**
     * Chess variant played (Standard, Chess960, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var variant: GameVariant = GameVariant.STANDARD,

    /**
     * The player with the white pieces.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "white_player_id", referencedColumnName = "id")
    var whitePlayer: Player,

    /**
     * The player with the black pieces.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "black_player_id", referencedColumnName = "id")
    var blackPlayer: Player,

    @ManyToOne(targetEntity = Event::class, fetch = FetchType.LAZY, optional = true, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name="event_id", nullable=true)
    var event: Event? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    ){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
