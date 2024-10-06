package com.michibaum.chess.apis.lichess

data class LichessAccountDto(
    val id: String,
    val username: String,
    val perfs: Perfs,
    val createdAt: Long,
    val profile: Profile,
    val seenAt: Long,
    val playTime: PlayTime,
    val url: String,
    val count: AccountCount,
)

data class Perfs(
    val bullet: Bullet,
    val blitz: Blitz,
    val rapid: Rapid,
    val classical: Classical,
    val correspondence: Correspondence,
    val puzzle: Puzzle,
)

data class Bullet(
    val games: Long,
    val rating: Long,
    val rd: Long,
    val prog: Long,
    val prov: Boolean,
)

data class Blitz(
    val games: Long,
    val rating: Long,
    val rd: Long,
    val prog: Long,
    val prov: Boolean,
)

data class Rapid(
    val games: Long,
    val rating: Long,
    val rd: Long,
    val prog: Long,
    val prov: Boolean,
)

data class Classical(
    val games: Long,
    val rating: Long,
    val rd: Long,
    val prog: Long,
    val prov: Boolean,
)

data class Correspondence(
    val games: Long,
    val rating: Long,
    val rd: Long,
    val prog: Long,
    val prov: Boolean,
)

data class Puzzle(
    val games: Long,
    val rating: Long,
    val rd: Long,
    val prog: Long,
    val prov: Boolean,
)

data class Profile(
    val location: String,
    val realName: String,
    val flag: String,
    val bio: String?,
    val links: String?,
)

data class PlayTime(
    val total: Long,
    val tv: Long,
)

data class AccountCount(
    val all: Long,
    val rated: Long,
    val ai: Long,
    val draw: Long,
    val drawH: Long,
    val loss: Long,
    val lossH: Long,
    val win: Long,
    val winH: Long,
    val bookmark: Long,
    val playing: Long,
    val import: Long,
    val me: Long,
)