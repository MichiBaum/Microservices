package com.michibaum.registry

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class RegistryApplication

fun main(args: Array<String>) {
	runApplication<RegistryApplication>(*args)
}
