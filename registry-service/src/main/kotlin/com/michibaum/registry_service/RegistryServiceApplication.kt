package com.michibaum.registry_service

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableEurekaServer
class RegistryServiceApplication

fun main(args: Array<String>) {
    runApplication<RegistryServiceApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}