package com.michibaum.chess.domain

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
data class Person(
    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val firstname: String,

    @Column(nullable = false)
    val lastname: String,

    @OneToMany(mappedBy="person", fetch = FetchType.LAZY)
    val accounts: Set<Account> = emptySet()
){

}
