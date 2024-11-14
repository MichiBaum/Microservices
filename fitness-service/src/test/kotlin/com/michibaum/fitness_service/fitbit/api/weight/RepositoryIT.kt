package com.michibaum.fitness_service.fitbit.api.weight

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class RepositoryIT {

    @Autowired
    lateinit var weightRepository: FitbitWeightRepository


}