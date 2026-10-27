package com.michibaum.music_service.app.library

import com.michibaum.music_service.apis.spotify.api.SpotifyApi
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

        val spotifyIds = validSpotifyArtists.map { it.id!! }
        val existingArtistSpotifies = artistSpotifyRepository.findBySpotifyIdIn(spotifyIds)
        val existingArtistSpotifyMap = existingArtistSpotifies.associateBy { it.spotifyId }

        val newSpotifyArtists = validSpotifyArtists.filter { !existingArtistSpotifyMap.containsKey(it.id) }

        val newArtistSpotifiesMap = if (newSpotifyArtists.isNotEmpty()) {
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
            artistSpotifyRepository.saveAll(spotifiesToSave).associateBy { it.spotifyId }
        } else {
            emptyMap()
        }

        val allArtists = validSpotifyArtists.mapNotNull { spotifyArtist ->
            existingArtistSpotifyMap[spotifyArtist.id]?.artist
                ?: newArtistSpotifiesMap[spotifyArtist.id]?.artist
        }

        val existingUserLibraryArtists = userLibraryArtistRepository.findByUserId(userId)
        val existingArtistIds = existingUserLibraryArtists.map { it.artist.id }.toSet()

        val missingUserLibraryArtists = allArtists
            .filter { artist -> artist.id != null && !existingArtistIds.contains(artist.id) }
            .map { artist ->
                UserLibraryArtist(
                    userId = userId,
                    artist = artist
                )
            }

        if (missingUserLibraryArtists.isNotEmpty()) {
            userLibraryArtistRepository.saveAll(missingUserLibraryArtists)
        }

        return getLibraryArtists(userId)
    }
}
