package com.michibaum.chess_service

import io.mockk.MockKMatcherScope

/**
 * Matches any iterable containing elements of the specified type [T].
 *
 * This function returns an iterable matcher that checks if the given iterable
 * contains elements of the specified type [T].
 *
 * @return An iterable of the specified type [T].
 */
inline fun <reified T> MockKMatcherScope.anyIterable(): Iterable<T> {
    //T is some Class like Account
    return match<Iterable<T>> {
        // it is some List like ArrayList
        it is Iterable<T> && it.all { item -> item is T }
    }
}