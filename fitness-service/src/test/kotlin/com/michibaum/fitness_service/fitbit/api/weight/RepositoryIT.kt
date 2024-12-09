package com.michibaum.fitness_service.fitbit.api.weight

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RepositoryIT {

    @Autowired
    lateinit var weightRepository: FitbitWeightRepository


}