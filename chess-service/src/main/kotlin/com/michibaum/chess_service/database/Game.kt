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
    val sourceType: SourceType,

    /**
     * The platform where the game was played (optional if not an online game).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    val platform: ChessPlatform? = null,

    /**
     * The unique identifier of the game on the external platform.
     */
    @Column(nullable = true)
    val externalGameId: String? = null,

    /**
     * Raw PGN content of the game.
     */
    @Lob
    @Column(nullable = false, length=2048)
    val pgn: String,

    @Column(nullable = true)
    var openingName: String? = null,

    /**
     * The result of the game.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val gameResult: GameResult,

    /**
     * How the game ended (e.g., checkmate, resignation, timeout).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    val termination: TerminationType? = null,

    /**
     * When the game was played.
     */
    @Column(nullable = false)
    val playedAt: java.time.LocalDateTime,

    /**
     * When the game was imported into the system.
     */
    @Column(nullable = false)
    val importedAt: java.time.LocalDateTime = java.time.LocalDateTime.now(),

    /**
     * Descriptive time control string (e.g., "300+5").
     */
    @Column(nullable = true)
    val timeControl: String? = null,

    /**
     * Category of the time control (Bullet, Blitz, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    val timeControlCategory: TimeControlCategory? = null,

    /**
     * Chess variant played (Standard, Chess960, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val variant: GameVariant = GameVariant.STANDARD,

    /**
     * The player with the white pieces.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "white_player_id", referencedColumnName = "id")
    val whitePlayer: Player,

    /**
     * The player with the black pieces.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "black_player_id", referencedColumnName = "id")
    val blackPlayer: Player,

    /**
     * Optional final FEN state.
     */
    @Column(nullable = true)
    val fen: String? = null,

    /**
     * Metadata for OTB games: Round number.
     */
    @Column(nullable = true)
    val round: String? = null,

    /**
     * Metadata for OTB games: Board number.
     */
    @Column(nullable = true)
    val boardNumber: Int? = null,

    @ManyToOne(targetEntity = Event::class, fetch = FetchType.LAZY, optional = true, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name="event_id", nullable=true)
    val event: Event? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    ){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
