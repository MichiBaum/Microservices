package com.michibaum.chess

import io.mockk.MockKMatcherScope

inline fun <reified T> MockKMatcherScope.anyIterable() = match<Iterable<T>> {
    it is T
}