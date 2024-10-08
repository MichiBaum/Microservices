package com.michibaum.chess

operator fun <T> Iterable<T>.get(index: Int): T{
    return this.elementAt(index)
}

/**
 * Performs the given function on each element of the iterable that is an instance of the specified type [T].
 *
 * This function iterates over the elements of the receiver [Iterable] and applies the [function] to
 * each element that is an instance of type [T]. Elements that are not of type [T] are ignored.
 *
 * @param R The type of the elements in the iterable.
 * @param T The target type of elements to apply the function on.
 * @param function The function to be executed on elements of type [T].
 * @return The original iterable [Iterable] of type [R].
 *
 * @sample
 * ```
 * val mixedList: List<Any> = listOf(1, "Kotlin", 2.5, "Java")
 * mixedList.doIfIsInstance<String> { println(it) }
 * // Output: Kotlin
 * //         Java
 * ```
 */
inline fun <reified R, reified T> Iterable<R>.doIfIsInstance(function: (T) -> Any): Iterable<R> {
    for (element in this)
        if (element is T)
            function(element)
    return this
}