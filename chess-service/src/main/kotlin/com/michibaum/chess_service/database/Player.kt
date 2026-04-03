package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="player")
@NamedEntityGraph(
    name = "with-account",
    attributeNodes = [NamedAttributeNode(value = "account")]
)
class Player(

    @Column(nullable = false)
    var username: String,

    @Column(nullable = true)
    var rating: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var pieceColor: PieceColor,

    @ManyToOne(targetEntity = Account::class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="account_id", nullable=true, foreignKey = ForeignKey(name = "fk_player_account"))
    var account: Account? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}