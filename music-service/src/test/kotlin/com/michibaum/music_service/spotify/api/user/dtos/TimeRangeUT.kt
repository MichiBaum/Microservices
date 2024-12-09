package com.michibaum.music_service.spotify.api.user.dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TimeRangeUT{

    @Test
    fun fromEnum(){
        // GIVEN
        val timeRange = TimeRange.LONG

        // WHEN
        val name = timeRange.name
        val toString = timeRange.toString()
        val property = timeRange.spotify

        // THEN
        assertEquals("LONG", name)
        assertEquals("LONG", toString)
        assertEquals("long_term", property)
    }

}