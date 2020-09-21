package com.michibaum.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableConfigServer
@RefreshScope
@EnableScheduling
class ConfigApplication

fun main(args: Array<String>) {
	runApplication<ConfigApplication>(*args)
}
