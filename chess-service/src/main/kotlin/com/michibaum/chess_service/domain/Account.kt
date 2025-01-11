package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name="account")
class Account(
    @Column(nullable = false)
    val platformId: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val username: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val platform: ChessPlatform,

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    val createdAt: LocalDate?,

    @ManyToOne(targetEntity = Person::class, fetch = FetchType.LAZY, optional = true, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name="person_id", nullable=true)
    val person: Person?,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}