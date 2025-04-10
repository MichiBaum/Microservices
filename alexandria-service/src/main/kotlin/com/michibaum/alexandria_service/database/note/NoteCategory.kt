package com.michibaum.alexandria_service.database.note

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.*
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name="note_category")
class NoteCategory(

    @Column(name="title")
    val title: String,

    @Column(name="description")
    val description: String,

    @Column(name="belongs_to")
    val belongsTo: String,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
) {}