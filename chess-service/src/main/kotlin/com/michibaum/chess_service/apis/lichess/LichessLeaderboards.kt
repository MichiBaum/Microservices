package com.michibaum.chess_service.apis.lichess

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(value = [
    "crazyhouse",
    "chess960",
    "kingOfTheHill",
    "threeCheck",
    "antichess",
    "atomic",
    "horde",
    "racingKings"
])
data class LichessLeaderboards(
    val bullet: List<LeaderboardBullet>,
    val blitz: List<LeaderboardBlitz>,
    val rapid: List<LeaderboardRapid>,
    val classical: List<LeaderboardClassical>,
    val ultraBullet: List<LeaderboardUltraBullet>,
) {

}
@JsonIgnoreProperties(value = ["perfs"])
data class LeaderboardBullet(
    val id: String,
    val username: String,
    val title: String?,
    val patron: Boolean?,
)

@JsonIgnoreProperties(value = ["perfs"])
data class LeaderboardBlitz(
    val id: String,
    val username: String,
    val title: String?,
    val online: Boolean?,
)

@JsonIgnoreProperties(value = ["perfs"])
data class LeaderboardRapid(
    val id: String,
    val username: String,
    val title: String?,
    val online: Boolean?,
    val patron: Boolean?,
)

@JsonIgnoreProperties(value = ["perfs"])
data class LeaderboardClassical(
    val id: String,
    val username: String,
    val title: String?,
    val patron: Boolean?,
    val online: Boolean?,
)

@JsonIgnoreProperties(value = ["perfs"])
data class LeaderboardUltraBullet(
    val id: String,
    val username: String,
    val title: String?,
    val patron: Boolean?,
    val online: Boolean?,
)