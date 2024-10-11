package com.michibaum.chess_service.apis.lichess

import com.fasterxml.jackson.annotation.JsonProperty

data class LichessStatsDto(
    @JsonProperty("user")
    val user: User,
    val perf: Perf,
    val rank: Long,
    val percentile: Double,
    val stat: Stat
)

data class User(
    @JsonProperty("name")
    val name: String
)

data class Perf(
    val glicko: Glicko,
    val nb: Long,
    val progress: Long
)

data class Glicko(
    val rating: Double,
    val deviation: Double,
    val provisional: Boolean
)

data class Stat(
    val perfType: PerfType,
    val highest: Highest?,
    val lowest: Lowest?,
    val id: String,
    val bestWins: BestWins,
    val worstLosses: WorstLosses,
    val count: StatsCount,
    val resultStreak: ResultStreak,
    val userId: UserId,
    val playStreak: PlayStreak
)

data class PerfType(
    val key: String,
    val name: String
)

data class Highest(
    val int: Double,
    val at: String,
    val gameId: String
)

data class Lowest(
    val int: Double,
    val at: String,
    val gameId: String
)

data class BestWins(
    val results: List<Result> = mutableListOf()
)

data class Result(
    val opRating: Long,
    val opId: OpId,
    val at: String,
    val gameId: String
)

data class OpId(
    val id: String,
    val name: String
)

data class WorstLosses(
    val results: List<Result> = mutableListOf()
)

data class StatsCount(
    val all: Long,
    val rated: Long,
    val win: Long,
    val loss: Long,
    val draw: Long,
    val tour: Long,
    val berserk: Long,
    val opAvg: Double,
    val seconds: Long,
    val disconnects: Long
)

data class ResultStreak(
    val win: Win,
    val loss: Loss
)

data class Win(
    val cur: Cur,
    val max: Max
)

data class Cur(
    val v: Long,
    val from: From?,
    val to: To?
)

data class Max(
    val v: Long,
    val from: From?,
    val to: To?,
)

data class From(
    val at: String,
    val gameId: String,
)

data class Loss(
    val cur: Cur,
    val max: Max,
)

data class To(
    val at: String,
    val gameId: String,
)

data class UserId(
    val id: String,
    val name: String,
    val title: String?,
)

data class PlayStreak(
    val nb: Nb,
    val time: Time,
    val lastDate: String?,
)

data class Nb(
    val cur: Cur,
    val max: Max,
)

data class Time(
    val cur: Cur,
    val max: Max,
)
