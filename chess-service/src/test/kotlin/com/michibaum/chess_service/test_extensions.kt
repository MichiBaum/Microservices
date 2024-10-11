package com.michibaum.chess_service

import io.mockk.MockKMatcherScope

inline fun <reified T> MockKMatcherScope.anyIterable() = match<Iterable<T>> {
    it is T
}