package com.michibaum.alexandria_service.database.note

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="note")
class Note (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){}