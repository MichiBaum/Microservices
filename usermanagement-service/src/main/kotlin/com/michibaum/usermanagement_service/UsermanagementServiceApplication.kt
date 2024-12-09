package com.michibaum.usermanagement_service

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(basePackages = ["com.michibaum.usermanagement_library", "com.michibaum.authentication_library"])
class UsermanagementServiceApplication

fun main(args: Array<String>) {
    runApplication<UsermanagementServiceApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}
