package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="player")
class Player(

    @Column(nullable = false)
    val username: String,

    @Column(nullable = false)
    val rating: Long,

    @Enumerated(EnumType.STRING)
    val pieceColor: PieceColor,

    @ManyToOne(targetEntity = Game::class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="game_id", nullable=false)
    val game: Game,

    @ManyToOne(targetEntity = Account::class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="account_id", nullable=false, foreignKey = ForeignKey(name = "fk_player_account"))
    val account: Account,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}