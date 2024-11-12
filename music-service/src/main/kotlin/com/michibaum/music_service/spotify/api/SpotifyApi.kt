package com.michibaum.music_service.spotify.api

import com.michibaum.music_service.spotify.api.profile.SpotifyProfileApi
import com.michibaum.music_service.spotify.api.user.SpotifyUserApi

interface SpotifyApi: SpotifyProfileApi, SpotifyUserApi {
}