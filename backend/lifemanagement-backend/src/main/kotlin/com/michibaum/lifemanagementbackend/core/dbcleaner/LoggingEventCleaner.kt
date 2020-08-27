package com.michibaum.lifemanagementbackend.core.dbcleaner

import com.michibaum.lifemanagementbackend.logs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.logs.repository.LoggingEventRepository
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.*

@Component
class LoggingEventCleaner(
    private val loggingEventRepository: LoggingEventRepository
) {

    private val logger = KotlinLogging.logger {}

    private enum class LoggingEventLevel(val levelName: String, val ageToDelete: Long){
        TRACE("TRACE", 907200025), // 1,5 weeks
        DEBUG("DEBUG", 907200025), // 1,5 weeks
        FINE("FINE", 907200025), // 1,5 weeks
        FINER("FINER", 907200025), // 1,5 weeks
        FINEST("FINEST", 907200025), // 1,5 weeks
        INFO("INFO", 907200025), // 1,5 weeks
        CONFIG("CONFIG", 907200025), // 1,5 weeks
        WARN("WARN", 907200025), // 1,5 weeks
        WARNING("WARNING", 907200025), // 1,5 weeks
        ERROR("ERROR", 2629800000), // 1 month
        FATAL("FATAL", 2629800000), // 1 month
        SEVERE("SEVERE", 2629800000), // 1 month;
    }

    @Scheduled(fixedRate = 24*60*60*1000) // Every day
    private fun deleteLoggingEvents(){
        this.logger.info("Hourly deleteLoggingEvents method starts to clear logs")
        var deletedLogsCounter: MutableMap<LoggingEventLevel, Long> = mutableMapOf()
        LoggingEventLevel.values().iterator().forEach { loggingEventLevel ->
            run {
                loggingEventRepository.findByLevelString(loggingEventLevel.levelName)
                    .forEach { loggingEvent: LoggingEvent ->
                        run {
                            if (loggingEvent.timestmp + loggingEventLevel.ageToDelete < Date().time) {
                                loggingEventRepository.delete(loggingEvent)
                                deletedLogsCounter = updateDeletedLogsCounter(deletedLogsCounter, loggingEventLevel)
                            }
                        }
                    }
            }
        }
        this.logger.info("Hourly deleteLoggingEvents method stats are:")
        deletedLogsCounter.keys.forEach{
            this.logger.info(" - Deleted " + deletedLogsCounter[it] + " logs with level " + it.levelName)

        }
    }

    private fun updateDeletedLogsCounter(
        deletedLogsCounter: MutableMap<LoggingEventLevel, Long>,
        loggingEventLevel: LoggingEventLevel
    ): MutableMap<LoggingEventLevel, Long> {
        if(deletedLogsCounter.containsKey(loggingEventLevel)){
            deletedLogsCounter[loggingEventLevel]?.let {
                deletedLogsCounter[loggingEventLevel] = it + 1
            }
        }else{
            deletedLogsCounter[loggingEventLevel] = 1
        }
        return deletedLogsCounter
    }

}
