package com.michibaum.music_service.database.spotify

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name="spotify_account_activated")
class SpotifyAccountActivated(

    @Column(name = "user_id", nullable = false, unique = true)
    val userId: String,

    @Column(name = "email", nullable = false, unique = true)
    val email: String,
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

) {
}