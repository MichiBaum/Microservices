package com.michibaum.vpn_service

import com.michibaum.vpn_service.app.kubernetes.KubernetesProperties
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(value = [KubernetesProperties::class])
class VpnServiceApplication

fun main(args: Array<String>) {
    runApplication<VpnServiceApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
