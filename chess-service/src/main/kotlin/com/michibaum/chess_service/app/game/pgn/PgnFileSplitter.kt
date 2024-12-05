package com.michibaum.chess_service.app.game.pgn

import java.io.BufferedReader
import java.io.StringReader

class PgnFileSplitter {
    private val attrRegex = """^\[.+\]$""".toRegex()
    private val moveRegex = """^\s?.+""".toRegex()
    private val termRegex = """^$""".toRegex()

    fun split(text: String): Sequence<String> {
        return split(BufferedReader(StringReader(text)))
    }

    fun split(reader: BufferedReader) = sequence {
        val newLines = mutableListOf<String>()
        var waitingForTermination = false

        var line = reader.readLine()
        while (line != null) {
            when {
                line.matches(attrRegex) -> newLines.add(line + "\n")
                line.matches(moveRegex) -> {
                    waitingForTermination = true
                    newLines[newLines.lastIndex] = newLines.last() + line
                }
                waitingForTermination && line.matches(termRegex) -> {
                    yield(newLines.joinToString(""))

                    newLines.clear()
                    waitingForTermination = false
                }
            }
            line = reader.readLine()
        }

        if (newLines.isNotEmpty()) {
            yield(newLines.joinToString(""))
        }
    }
}