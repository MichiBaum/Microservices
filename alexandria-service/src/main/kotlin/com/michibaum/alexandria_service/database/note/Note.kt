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

    @Column(name="belongs_to")
    val belongsTo: String,
    
    @ManyToMany(mappedBy = "notes", fetch = FetchType.LAZY)
    val categories: List<NoteCategory>,
    
    // TODO Sharing of notes

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

){}