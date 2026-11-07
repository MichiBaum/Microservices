package com.michibaum.music_service.app.playlist

import java.util.UUID

data class CreatePlaylistRequest(
    val name: String,
    val description: String? = null,
    val coverImageUrl: String? = null
)

data class UpdatePlaylistRequest(
    val name: String? = null,
    val description: String? = null,
    val coverImageUrl: String? = null
)

data class AddTrackRequest(
    val trackId: UUID,
    val position: Int? = null
)

data class SyncPlaylistsRequest(
    val spotifyPlaylistIds: List<String>? = null
)
