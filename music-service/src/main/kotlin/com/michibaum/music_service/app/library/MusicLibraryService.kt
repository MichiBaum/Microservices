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
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(MusicLibraryService::class.java)

    @Observed(name = "music.library.get.artists")
    @Transactional(readOnly = true)
    fun getLibraryArtists(userId: String): List<ArtistDto> {
        return userLibraryArtistRepository.findByUserId(userId)
            .map { userLibraryArtist -> artistConverter.toDto(userLibraryArtist.artist) }
    }

    @Observed(name = "music.library.sync.artists")
    fun syncLibraryArtists(userId: String): List<ArtistDto> {
        val fetchedArtists = fetchSpotifyArtistsOrNull(userId)
        val validSpotifyArtists = fetchedArtists
            ?.filter { !it.id.isNullOrBlank() && !it.name.isNullOrBlank() }
            ?.distinctBy { it.id }

        if (validSpotifyArtists.isNullOrEmpty()) {
            return getLibraryArtists(userId)
        }

        return persistSyncedArtists(userId, validSpotifyArtists)
    }

    private fun fetchSpotifyArtistsOrNull(userId: String): List<ArtistObject>? {
        val credentials = spotifyOAuthService.getActiveToken(userId) ?: return null

        return try {
            fetchAllFollowedArtists(credentials)
        } catch (e: Exception) {
            logger.error("Failed to fetch followed artists from Spotify for user $userId", e)
            null
        }
    }

    private fun fetchAllFollowedArtists(credentials: SpotifyOAuthCredentials): List<ArtistObject> {
        val allArtists = mutableListOf<ArtistObject>()
        var afterCursor: String? = null

        do {
            val response = spotifyApi.myFollowedArtists(credentials, afterCursor)
            val paging = response?.artists ?: break
            val items = paging.items ?: emptyList()
            allArtists.addAll(items)

            if (items.isEmpty() || paging.next == null) {
                break
            }

            val nextAfter = paging.cursors?.after
            if (nextAfter == afterCursor) {
                break
            }
            afterCursor = nextAfter
        } while (!afterCursor.isNullOrBlank())

        return allArtists
    }

    @Transactional
    fun persistSyncedArtists(userId: String, validSpotifyArtists: List<ArtistObject>): List<ArtistDto> {
        val spotifyIds = validSpotifyArtists.mapNotNull { it.id }
        val existingArtistSpotifyMap = artistSpotifyRepository.findBySpotifyIdIn(spotifyIds)
            .associateBy { it.spotifyId }

        val (existingSpotifyArtists, newSpotifyArtists) = validSpotifyArtists.partition {
            existingArtistSpotifyMap.containsKey(it.id)
        }

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

            val artistChanged = existingArtist.name != newName
            if (artistChanged) {
                val updatedArtist = existingArtist.copy(name = newName)
                val updatedArtistSpotify = existingArtistSpotify.copy(artist = updatedArtist)
                artistsToUpdate.add(updatedArtist)
                artistSpotifiesToUpdate.add(updatedArtistSpotify)
                updatedMap[spotifyId] = updatedArtistSpotify
            } else {
                updatedMap[spotifyId] = existingArtistSpotify
            }
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
                spotifyId = spotifyArtist.id!!
            )
        }
        return artistSpotifyRepository.saveAll(spotifiesToSave).associateBy { it.spotifyId }
    }
}
