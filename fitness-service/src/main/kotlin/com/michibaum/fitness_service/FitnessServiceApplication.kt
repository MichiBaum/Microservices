package com.michibaum.fitness_service

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
class FitnessServiceApplication

fun main(args: Array<String>) {
    runApplication<FitnessServiceApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}