package com.michibaum.alexandria_service

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableConfigurationProperties
class AlexandriaServiceApplication

fun main(args: Array<String>) {
    runApplication<AlexandriaServiceApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}