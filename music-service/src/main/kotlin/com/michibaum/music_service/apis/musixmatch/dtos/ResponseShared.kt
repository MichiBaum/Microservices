package com.michibaum.music_service.apis.musixmatch.dtos

import com.fasterxml.jackson.annotation.JsonProperty



data class Header(
    @JsonProperty("execute_time")
    val executeTime: Double,
    @JsonProperty("status_code")
    val statusCode: Long,
)