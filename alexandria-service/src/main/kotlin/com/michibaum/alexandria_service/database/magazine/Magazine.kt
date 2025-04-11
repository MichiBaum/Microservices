package com.michibaum.alexandria_service.database.magazine

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="magazine")
class Magazine (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){}