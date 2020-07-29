package com.michibaum.lifemanagementbackend.core.dbcleaner

import com.michibaum.lifemanagementbackend.logs.domain.LoggingEvent
import com.michibaum.lifemanagementbackend.logs.repository.LoggingEventRepository
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

@Service
class LoggingEventCleaner(
    private val loggingEventRepository: LoggingEventRepository
) {

    private val logger = KotlinLogging.logger {}

    private enum class LoggingEventLevel(val levelName: String, val ageToDelete: Long){
        TRACE("TRACE", 21600000), // 6 hours
        DEBUG("DEBUG", 21600000), // 6 hours
        FINE("FINE", 21600000), // 6 hours
        FINER("FINER", 21600000), // 6 hours
        FINEST("FINEST", 21600000), // 6 hours
        INFO("INFO", 259200000), // 3 days
        CONFIG("CONFIG", 259200000), // 3 days
        WARN("WARN", 907200025), // 1,5 weeks
        WARNING("WARNING", 907200025), // 1,5 weeks
        ERROR("ERROR", 2629800000), // 1 month
        FATAL("FATAL", 2629800000), // 1 month
        SEVERE("SEVERE", 2629800000), // 1 month;
    }

    @Scheduled(fixedRate = 60*60*1000) // Every hour
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
