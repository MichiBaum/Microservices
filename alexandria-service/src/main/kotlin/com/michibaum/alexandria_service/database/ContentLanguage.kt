package com.michibaum.alexandria_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name="content_language")
class ContentLanguage (

    @Column(name="language", nullable = false)
    val language: String,
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
){
    
}