package com.michibaum.gateway

import brave.sampler.Sampler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@RefreshScope
@EnableScheduling
class GatewayApplication {

	@Bean
	fun defaultSampler(): Sampler = Sampler.ALWAYS_SAMPLE

}

fun main(args: Array<String>) {
	runApplication<GatewayApplication>(*args)
}
