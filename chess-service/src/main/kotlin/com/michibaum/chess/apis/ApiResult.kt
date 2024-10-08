package com.michibaum.chess.apis

import org.slf4j.LoggerFactory


sealed class ApiResult<T>

data class Success<T>(val result: T) : ApiResult<T>()

interface Loggable{
    fun log()
}

data class Error<T>(val error: String): ApiResult<T>(), Loggable{
    private val logger = LoggerFactory.getLogger(Error::class.java)
    override fun log(){
        logger.error(error)
    }
}

data class Exception<T>(val message: String, val throwable: Throwable): ApiResult<T>(), Loggable{
    private val logger = LoggerFactory.getLogger(Exception::class.java)
    override fun log(){
        logger.error(message + " " + throwable.message, throwable)
    }
}