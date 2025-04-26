package com.michibaum.registry_service

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableEurekaServer
@EnableDiscoveryClient
class RegistryServiceApplication

fun main(args: Array<String>) {
    runApplication<RegistryServiceApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}