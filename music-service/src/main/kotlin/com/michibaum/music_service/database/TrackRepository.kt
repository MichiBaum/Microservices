package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface TrackRepository: JpaRepository<Track, UUID> {
    @Query("SELECT ts.track FROM TrackSpotify ts WHERE ts.spotifyId = :spotifyId")
    fun findBySpotifyId(spotifyId: String): Track?

    @Query("SELECT t FROM Track t WHERE LOWER(t.name) = LOWER(:name)")
    fun findByNameAndArtist(name: String): Track?

    @Query("SELECT t FROM Track t JOIN t.artists a WHERE LOWER(t.name) = LOWER(:name) AND LOWER(a.name) = LOWER(:artistName)")
    fun findByNameAndArtist(name: String, artistName: String): Track?
}