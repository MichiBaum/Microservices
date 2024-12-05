package com.michibaum.chess_service.app.game.pgn

class PgnMatchParser {
    private val moveRegex = """\d+\.\s+""".toRegex()

    fun parse(text: String): Match {
        val attributes = parseAttributes(text)
        return Match(
            event = attributes.getOrDefault("Event", ""),
            site = attributes.getOrDefault("Site", ""),
            round = attributes.getOrDefault("Round", ""),
            date = attributes.getOrDefault("Date", ""),
            white = attributes.getOrDefault("White", ""),
            black = attributes.getOrDefault("Black", ""),
            result = attributes.getOrDefault("Result", ""),
            whiteElo = attributes.getOrDefault("WhiteElo", ""),
            blackElo = attributes.getOrDefault("BlackElo", ""),
            timeControl = attributes.getOrDefault("TimeControl", ""),
            link = attributes.getOrDefault("Link", ""),
            moves = parseMoves(text),
        )
    }

    fun parseAttributes(text: String): Map<String, String> {
        val attributes = mutableMapOf<String, String>()
        text.lines().forEach {
            if (it.startsWith("[") && it.endsWith("]")) {
                val attribute = it.trim('[', ']')
                val (name, value) = attribute.split(" ", limit = 2)

                attributes.put(name, value.trim('"'))
            }
        }

        return attributes
    }

    fun parseMoves(text: String): List<Move> {
        val line = text.lines().find {
            it.startsWith("1.")
        } ?: ""

        return line.split(moveRegex)
            .filter { it != "" }
            .mapIndexed { i, move ->
                Move(
                    number = i + 1,
                    color = if (i % 2 == 0) "white" else "black",
                    text = move.trim())
            }
    }
}