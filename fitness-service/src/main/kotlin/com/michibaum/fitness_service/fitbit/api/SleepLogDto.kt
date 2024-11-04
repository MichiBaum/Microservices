package com.michibaum.fitness_service.fitbit.api

data class SleepLogDto(
    val sleep: List<SleepDto>,
)

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

data class Levels(
    val data: List<Data>,
    val shortData: List<ShortData>?,
    val summary: Summary,
)

data class Data(
    val dateTime: String,
    val level: String,
    val seconds: Long,
)

data class ShortData(
    val dateTime: String,
    val level: String,
    val seconds: Long,
)

data class Summary(
    val deep: Deep?,
    val light: Light?,
    val rem: Rem?,
    val wake: Wake?,
    val asleep: Asleep?,
    val awake: Awake?,
    val restless: Restless?,
)

data class Deep(
    val count: Long,
    val minutes: Long,
    val thirtyDayAvgMinutes: Long,
)

data class Light(
    val count: Long,
    val minutes: Long,
    val thirtyDayAvgMinutes: Long,
)

data class Rem(
    val count: Long,
    val minutes: Long,
    val thirtyDayAvgMinutes: Long,
)

data class Wake(
    val count: Long,
    val minutes: Long,
    val thirtyDayAvgMinutes: Long,
)

data class Asleep(
    val count: Long,
    val minutes: Long,
)

data class Awake(
    val count: Long,
    val minutes: Long,
)

data class Restless(
    val count: Long,
    val minutes: Long,
)