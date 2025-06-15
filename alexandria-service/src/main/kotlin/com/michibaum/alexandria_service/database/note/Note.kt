package com.michibaum.alexandria_service.database.note

import com.michibaum.alexandria_service.database.IdNullException
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="note")
class Note ( // TODO add Hibernate Envers, Author

    @Column(name="text")
    val text: String,

    @Column(name="encrypted")
    val encryped: Boolean,

    @Column(name="belongs_to")
    val belongsTo: String,
    
    // TODO Sharing of notes

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}