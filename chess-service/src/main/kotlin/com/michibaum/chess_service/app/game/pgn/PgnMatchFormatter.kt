package com.michibaum.chess_service.app.game.pgn

class PgnMatchFormatter {
    val MAX_LINE_LEN = 74 // not including newline char

    fun format(match: Match): String {
        return "${attributes(match)}\n${moves(match)}"
    }

    fun attributes(match: Match) = """
        [Event "${match.event}"]
        [Site "${match.site}"]
        [Round "${match.round}"]
        [Date "${match.date}"]
        [White "${match.white}"]
        [Black "${match.black}"]
        """.trimIndent()

    fun moves(match: Match): String {
        var lineLen = 0
        val builder = StringBuilder()

        match.moves.forEachIndexed { i, move ->
            if (i > 0) {
                // except for first move, prepend a space
                // character to separate moves
                if (lineLen + 1 > MAX_LINE_LEN) {
                    lineLen = 0
                    builder.append("\n")
                }
                lineLen++
                builder.append(" ")
            }

            val number = "${i + 1}."
            if (lineLen + number.length > MAX_LINE_LEN) {
                lineLen = 0
                builder.append("\n")
            }

            lineLen += number.length
            builder.append(number)

            if (lineLen + 1 > MAX_LINE_LEN) {
                lineLen = 0
                builder.append("\n")
            }
            lineLen++
            builder.append(" ")

            val text = move.text
            if (lineLen + text.length > MAX_LINE_LEN) {
                lineLen = 0
                builder.append("\n")
            }

            lineLen += text.length
            builder.append(text)
        }

        return builder.toString()
    }
}