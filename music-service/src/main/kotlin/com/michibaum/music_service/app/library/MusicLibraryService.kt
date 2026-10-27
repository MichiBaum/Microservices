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

        for (spotifyArtist in spotifyArtists) {
            val spotifyId = spotifyArtist.id ?: continue
            val artistName = spotifyArtist.name ?: continue

            val artistSpotify = artistSpotifyRepository.findBySpotifyId(spotifyId)
            val artist = if (artistSpotify != null) {
                artistSpotify.artist
            } else {
                val newArtist = artistRepository.save(
                    Artist(name = artistName, isni = null)
                )
                artistSpotifyRepository.save(
                    ArtistSpotify(
                        artist = newArtist,
                        spotifyId = spotifyId,
                        followers = spotifyArtist.followers?.total?.toLong()
                    )
                )
                newArtist
            }

            val artistId = artist.id ?: continue
            if (!userLibraryArtistRepository.existsByUserIdAndArtistId(userId, artistId)) {
                userLibraryArtistRepository.save(
                    UserLibraryArtist(
                        userId = userId,
                        artist = artist
                    )
                )
            }
        }

        return getLibraryArtists(userId)
    }
}
