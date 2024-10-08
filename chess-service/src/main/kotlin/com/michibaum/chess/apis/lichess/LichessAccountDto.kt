package com.michibaum.chess.apis.lichess

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(value = ["perfs"], ignoreUnknown = true)
data class LichessAccountDto(
    val id: String,
    val username: String,
    val title: String?,
    val createdAt: Long,
    val profile: Profile?,
    val seenAt: Long,
    val url: String?,
)

data class Profile(
    val location: String?,
    val realName: String?,
    val flag: String?,
    val bio: String?,
    val links: String?,
)
