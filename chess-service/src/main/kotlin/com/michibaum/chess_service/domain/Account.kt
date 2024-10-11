package com.michibaum.chess_service.domain

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
class Account(
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

    @ManyToOne(targetEntity = Person::class, fetch = FetchType.LAZY, optional = true, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name="person_id", nullable=true)
    val person: Person?,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], targetEntity = Game::class)
    @JoinTable(name = "ACCOUNT_GAME_MAPPING", joinColumns = [JoinColumn(name = "account_id")], inverseJoinColumns = [JoinColumn(name = "game_id")])
    val games: Set<Game>,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

) {

}