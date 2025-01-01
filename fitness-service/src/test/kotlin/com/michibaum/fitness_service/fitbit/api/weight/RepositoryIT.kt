package com.michibaum.fitness_service.fitbit.api.weight

import com.michibaum.fitness_service.TestcontainersConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestcontainersConfiguration
class RepositoryIT {

    @Autowired
    lateinit var weightRepository: FitbitWeightRepository


}