package com.michibaum.chess

operator fun <T> Iterable<T>.get(index: Int): T{
    return this.elementAt(index)
}