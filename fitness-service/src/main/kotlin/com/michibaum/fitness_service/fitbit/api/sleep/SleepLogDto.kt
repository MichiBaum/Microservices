package com.michibaum.fitness_service.fitbit.api.sleep

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class SleepLogDto(
    val sleep: List<SleepDto> = mutableListOf(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleepDto(
    val dateOfSleep: String,
    val duration: Long,
    val efficiency: Long,
    val endTime: String,
    val infoCode: Long,
    val isMainSleep: Boolean,
    val levels: Levels,
    val logId: Long,
    val logType: String,
    val minutesAfterWakeup: Long,
    val minutesAsleep: Long,
    val minutesAwake: Long,
    val minutesToFallAsleep: Long,
    val startTime: String,
    val timeInBed: Long,
    val type: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Levels(
    val data: List<Data>?,
    val shortData: List<Data>?,
    val summary: Summary,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Data(
    val dateTime: String,
    val level: String,
    val seconds: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Summary(
    val deep: Deep?,
    val light: Light?,
    val rem: Rem?,
    val wake: Wake?,
    val asleep: Asleep?,
    val awake: Awake?,
    val restless: Restless?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Deep(
    val count: Long,
    val minutes: Long,
    val thirtyDayAvgMinutes: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Light(
    val count: Long,
    val minutes: Long,
    val thirtyDayAvgMinutes: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Rem(
    val count: Long,
    val minutes: Long,
    val thirtyDayAvgMinutes: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Wake(
    val count: Long,
    val minutes: Long,
    val thirtyDayAvgMinutes: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Asleep(
    val count: Long,
    val minutes: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Awake(
    val count: Long,
    val minutes: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Restless(
    val count: Long,
    val minutes: Long,
)