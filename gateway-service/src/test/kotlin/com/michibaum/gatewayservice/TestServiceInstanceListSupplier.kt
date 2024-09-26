package com.michibaum.gatewayservice

import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import reactor.core.publisher.Flux


class TestServiceInstanceListSupplier(
    private var serviceId: String,
    private var ports: IntArray
): ServiceInstanceListSupplier {

    override fun getServiceId(): String {
        return serviceId
    }

    override fun get(): Flux<List<ServiceInstance>> {
        val result: MutableList<ServiceInstance> = ArrayList()
        for (i in ports.indices) {
            result.add(DefaultServiceInstance(serviceId + i, getServiceId(), "localhost", ports[i], false))
        }
        return Flux.just(result)
    }
}