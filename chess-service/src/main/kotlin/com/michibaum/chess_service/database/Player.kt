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

    @Column(nullable = true)
    val username: String? = null,

    @Column(nullable = true)
    val rating: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val pieceColor: PieceColor,

    @ManyToOne(targetEntity = Account::class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="account_id", nullable=true, foreignKey = ForeignKey(name = "fk_player_account"))
    val account: Account? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}