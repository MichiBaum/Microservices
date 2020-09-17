package com.michibaum.registry

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableEurekaServer
@RefreshScope
@EnableScheduling
class RegistryApplication

fun main(args: Array<String>) {
	runApplication<RegistryApplication>(*args)
}
