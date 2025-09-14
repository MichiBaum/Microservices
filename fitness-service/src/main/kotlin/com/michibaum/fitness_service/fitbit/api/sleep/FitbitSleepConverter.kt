package com.michibaum.fitness_service.fitbit.api.sleep

import com.michibaum.fitness_service.api.sleep.SleepStage
import com.michibaum.fitness_service.api.sleep.Stage
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class FitbitSleepConverter { // https://dev.fitbit.com/build/reference/web-api/developer-guide/best-practices/#Interpreting-the-Sleep-Stage-and-Short-Data
    fun toDomain(sleep: SleepDto, userId: String): FitbitSleep {
        return FitbitSleep(
            startTime = LocalDateTime.parse(sleep.startTime),
            endTime = LocalDateTime.parse(sleep.endTime),
            duration = sleep.duration,
            fitbitId = sleep.logId,
            userId = userId
        )
    }


    fun toDomain(sleep: SleepDto, fitbitSleep: FitbitSleep): Set<SleepStage> {
        val dataStages = sleep.levels.data?.let { data -> sleepStages(data, fitbitSleep)}
        val shortdataStages = sleep.levels.shortData?.let { shortData -> sleepStages(shortData, fitbitSleep)}
        return combine(dataStages, shortdataStages)
    }
    
    private fun combine(dataStages: Set<SleepStage>?, shortdataStages: Set<SleepStage>?): Set<SleepStage> {
        return when {
            dataStages != null && shortdataStages != null -> dataStages + shortdataStages
            dataStages != null -> dataStages
            shortdataStages != null -> shortdataStages
            else -> emptySet()
        }
    }
    
    private fun sleepStages(data: List<Data>, fitbitSleep: FitbitSleep): Set<SleepStage> {
        return data.map { level ->
            val start = LocalDateTime.parse(level.dateTime)
            SleepStage(
                start = start,
                end = start.plusSeconds(level.seconds),
                stage = stage(level.level),
                duration = level.seconds,
                sleep = fitbitSleep
            )
        }.toSet()
    }

    private fun stage(level: String): Stage {
        // https://dev.fitbit.com/build/reference/web-api/sleep/get-sleep-log-by-date-range/
        return when (level){
            // Classic
            "restless" -> Stage.RESTLESS
            "asleep" -> Stage.ASLEEP
            "awake" -> Stage.AWAKE

            // Stages
            "deep" -> Stage.DEEP
            "light" -> Stage.LIGHT
            "rem" -> Stage.REM
            "wake" -> Stage.WAKE
            else -> Stage.UNKNOWN
        }
    }

}