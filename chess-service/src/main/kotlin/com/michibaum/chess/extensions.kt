package com.michibaum.chess

/**
 * Returns the element at the specified index in the iterable.
 *
 * @param index the index of the element to return
 * @return the element at the specified index
 * @throws IndexOutOfBoundsException if the index is out of bounds (index < 0 || index >= size)
 */
operator fun <T> Iterable<T>.get(index: Int): T {
    if (index < 0 || index >= this.count()) {
        throw IndexOutOfBoundsException("Index $index is out of bounds for this iterable.")
    }
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