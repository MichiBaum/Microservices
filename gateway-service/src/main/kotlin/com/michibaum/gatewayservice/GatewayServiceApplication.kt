package com.michibaum.gatewayservice

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(basePackages = ["com.michibaum.gatewayservice.config.feign", "com.michibaum.authentication_library"])
class GatewayServiceApplication

fun main(args: Array<String>) {
	runApplication<GatewayServiceApplication>(*args){
		setBannerMode(Banner.Mode.OFF)
	}
}

