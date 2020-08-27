package com.michibaum.lifemanagementbackend.core.dbcleaner

import com.michibaum.lifemanagementbackend.core.date.RealDate
import com.michibaum.lifemanagementbackend.user.repository.JWTRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTScheduledInvalidator(
    private val jwtRepository: JWTRepository
) {

    @Scheduled(fixedRate = 20*60*1000) // Every twenty minutes
    private fun invalidateOldJWTs(){
        val activeJWTs = jwtRepository.findByActive(true)
        activeJWTs.forEach {
            if(it.expiresAt < RealDate().millisecconds){
                it.active = false
                jwtRepository.save(it)
            }
        }
    }

}
