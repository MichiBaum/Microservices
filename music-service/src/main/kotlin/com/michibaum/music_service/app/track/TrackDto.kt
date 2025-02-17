package com.michibaum.music_service.app.track

data class TrackDto(
    val id: String,
    val name: String,
    val isrc: Set<String>
)
