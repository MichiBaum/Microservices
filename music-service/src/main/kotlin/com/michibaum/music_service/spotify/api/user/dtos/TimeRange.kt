package com.michibaum.music_service.spotify.api.user.dtos

enum class TimeRange(val spotify: String) {
    LONG("long_term"),      // ~1 year
    MEDIUM("medium_term"),    // ~6 months
    SHORT("short_term"),     // ~4 weeks
}