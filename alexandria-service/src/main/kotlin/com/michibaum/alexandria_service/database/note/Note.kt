package com.michibaum.alexandria_service.database.note

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="note")
class Note ( // TODO add Hibernate Envers, Author

    @Column(name="text")
    val text: String,

    @Column(name="encrypted")
    val encryped: Boolean,

    // TODO Sharing of notes

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

){}