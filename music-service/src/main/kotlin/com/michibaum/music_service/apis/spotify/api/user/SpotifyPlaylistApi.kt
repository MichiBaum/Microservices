package com.michibaum.music_service.apis.spotify.api.user

import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import com.michibaum.music_service.apis.spotify.generated.model.PlaylistObject
import com.michibaum.music_service.apis.spotify.generated.model.PlaylistTrackObject
import com.michibaum.music_service.apis.spotify.generated.model.SimplifiedPlaylistObject

interface SpotifyPlaylistApi {
    fun myPlaylists(credentials: SpotifyOAuthCredentials): List<SimplifiedPlaylistObject>
    fun getPlaylist(playlistId: String, credentials: SpotifyOAuthCredentials): PlaylistObject?
    fun getPlaylistTracks(playlistId: String, credentials: SpotifyOAuthCredentials): List<PlaylistTrackObject>
}
