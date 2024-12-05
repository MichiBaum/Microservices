package com.michibaum.chess_service.app.game.pgn

data class Match(
    val event: String,
    val site: String,
    val date: String,
    val round: String,
    val white: String,
    val black: String,
    val result: String,
    val whiteElo: String,
    val blackElo: String,
    val timeControl: String,
    val link: String,
    val moves: List<Move>)

data class Move(
    val number: Int,
    val color: String,
    val text: String)

