package com.michibaum.music_service.app.library

import com.michibaum.music_service.apis.spotify.api.SpotifyApi
import com.michibaum.music_service.apis.spotify.generated.model.ArtistObject
import com.michibaum.music_service.apis.spotify.oauth.SpotifyOAuthService
import com.michibaum.music_service.app.artist.ArtistConverter
import com.michibaum.music_service.app.artist.ArtistDto
import com.michibaum.music_service.database.Artist
import com.michibaum.music_service.database.ArtistRepository
import com.michibaum.music_service.database.UserLibraryArtist
import com.michibaum.music_service.database.UserLibraryArtistRepository
import com.michibaum.music_service.database.spotify.ArtistSpotify
import com.michibaum.music_service.database.spotify.ArtistSpotifyRepository
import io.micrometer.observation.annotation.Observed
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MusicLibraryService(
    private val userLibraryArtistRepository: UserLibraryArtistRepository,
    private val artistRepository: ArtistRepository,
    private val artistSpotifyRepository: ArtistSpotifyRepository,
    private val spotifyOAuthService: SpotifyOAuthService,
    private val spotifyApi: SpotifyApi,
    private val artistConverter: ArtistConverter,
) {

    @Observed(name = "music.library.get.artists")
    @Transactional(readOnly = true)
    fun getLibraryArtists(userId: String): List<ArtistDto> {
        return userLibraryArtistRepository.findByUserId(userId)
            .map { userLibraryArtist -> artistConverter.toDto(userLibraryArtist.artist) }
    }

    @Observed(name = "music.library.sync.artists")
    @Transactional
    fun syncLibraryArtists(userId: String): List<ArtistDto> {
        val credentials = spotifyOAuthService.activeCredentialsByUser(userId)
            ?: return getLibraryArtists(userId)

        val response = spotifyApi.myFollowedArtists(credentials)
        val spotifyArtists = response?.artists?.items ?: emptyList()

        val validSpotifyArtists = spotifyArtists
            .filter { !it.id.isNullOrBlank() && !it.name.isNullOrBlank() }
            .distinctBy { it.id }

        if (validSpotifyArtists.isEmpty()) {
            return getLibraryArtists(userId)
        }

        val spotifyIds = validSpotifyArtists.mapNotNull { it.id }
        val existingArtistSpotifyMap = artistSpotifyRepository.findBySpotifyIdIn(spotifyIds)
            .associateBy { it.spotifyId }

        val newSpotifyArtists = validSpotifyArtists.filter { !existingArtistSpotifyMap.containsKey(it.id) }
        val existingSpotifyArtists = validSpotifyArtists.filter { existingArtistSpotifyMap.containsKey(it.id) }

        val updatedArtistSpotifyMap = updateExistingSpotifyArtists(existingSpotifyArtists, existingArtistSpotifyMap)
        val newArtistSpotifyMap = createNewSpotifyArtists(newSpotifyArtists)

        val allArtists = validSpotifyArtists.mapNotNull { spotifyArtist ->
            updatedArtistSpotifyMap[spotifyArtist.id]?.artist
                ?: newArtistSpotifyMap[spotifyArtist.id]?.artist
        }

        val existingUserLibraryArtists = userLibraryArtistRepository.findByUserId(userId)
        val existingArtistIds = existingUserLibraryArtists.mapNotNull { it.artist.id }.toSet()

        val missingUserLibraryArtists = allArtists
            .filter { artist -> artist.id != null && !existingArtistIds.contains(artist.id) }
            .map { artist -> UserLibraryArtist(userId = userId, artist = artist) }

        val savedUserLibraryArtists = if (missingUserLibraryArtists.isNotEmpty()) {
            userLibraryArtistRepository.saveAll(missingUserLibraryArtists)
        } else {
            emptyList()
        }

        return (existingUserLibraryArtists + savedUserLibraryArtists)
            .map { userLibraryArtist -> artistConverter.toDto(userLibraryArtist.artist) }
    }

    private fun updateExistingSpotifyArtists(
        existingSpotifyArtists: List<ArtistObject>,
        existingArtistSpotifyMap: Map<String, ArtistSpotify>
    ): Map<String, ArtistSpotify> {
        if (existingSpotifyArtists.isEmpty()) {
            return emptyMap()
        }

        val artistsToUpdate = mutableListOf<Artist>()
        val artistSpotifiesToUpdate = mutableListOf<ArtistSpotify>()
        val updatedMap = mutableMapOf<String, ArtistSpotify>()

        for (spotifyArtist in existingSpotifyArtists) {
            val spotifyId = spotifyArtist.id ?: continue
            val existingArtistSpotify = existingArtistSpotifyMap[spotifyId] ?: continue
            val existingArtist = existingArtistSpotify.artist
            val newName = spotifyArtist.name ?: existingArtist.name
            val newFollowers = spotifyArtist.followers?.total?.toLong()

            val artistChanged = existingArtist.name != newName
            val updatedArtist = if (artistChanged) {
                existingArtist.copy(name = newName)
            } else {
                existingArtist
            }

            if (artistChanged) {
                artistsToUpdate.add(updatedArtist)
            }

            val spotifyChanged = artistChanged || existingArtistSpotify.followers != newFollowers
            val updatedArtistSpotify = if (spotifyChanged) {
                existingArtistSpotify.copy(artist = updatedArtist, followers = newFollowers)
            } else {
                existingArtistSpotify
            }

            if (spotifyChanged) {
                artistSpotifiesToUpdate.add(updatedArtistSpotify)
            }

            updatedMap[spotifyId] = updatedArtistSpotify
        }

        if (artistsToUpdate.isNotEmpty()) {
            artistRepository.saveAll(artistsToUpdate)
        }

        if (artistSpotifiesToUpdate.isNotEmpty()) {
            artistSpotifyRepository.saveAll(artistSpotifiesToUpdate)
        }

        return updatedMap
    }

    private fun createNewSpotifyArtists(
        newSpotifyArtists: List<ArtistObject>
    ): Map<String, ArtistSpotify> {
        if (newSpotifyArtists.isEmpty()) {
            return emptyMap()
        }

        val newArtists = artistRepository.saveAll(
            newSpotifyArtists.map { Artist(name = it.name!!, isni = null) }
        )
        val spotifiesToSave = newSpotifyArtists.zip(newArtists).map { (spotifyArtist, savedArtist) ->
            ArtistSpotify(
                artist = savedArtist,
                spotifyId = spotifyArtist.id!!,
                followers = spotifyArtist.followers?.total?.toLong()
            )
        }
        return artistSpotifyRepository.saveAll(spotifiesToSave).associateBy { it.spotifyId }
    }
}
