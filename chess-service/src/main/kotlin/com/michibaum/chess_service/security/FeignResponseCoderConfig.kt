package com.michibaum.chess_service.security

import feign.codec.Decoder
import feign.codec.Encoder
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * https://github.com/spring-cloud/spring-cloud-openfeign/issues/235
 * fanticat opened this issue on Oct 22, 2019 · 10 comments
 */
@Configuration
class FeignResponseCoderConfig {

    private val messageConverters = ObjectFactory { HttpMessageConverters() }

    @Bean
    fun feignEncoder(): Encoder {
        return SpringEncoder(messageConverters)
    }

    @Bean
    fun feignDecoder(): Decoder {
        return SpringDecoder(messageConverters)
    }
}
