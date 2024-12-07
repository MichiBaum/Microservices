package com.michibaum.chess_service.apis.fide

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "playerslist")
data class FidePlayer(
    @JacksonXmlProperty(localName = "fideid")
    val fideid: String,
    @JacksonXmlProperty(localName = "name")
    val name: String,
    @JacksonXmlProperty(localName = "country")
    val country: String,
    @JacksonXmlProperty(localName = "sex")
    val sex: String,
    @JacksonXmlProperty(localName = "title")
    val title: String,
    @JacksonXmlProperty(localName = "w_title")
    val wTitle: String,
    @JacksonXmlProperty(localName = "o_title")
    val oTitle: String,
    @JacksonXmlProperty(localName = "foa_title")
    val foaTitle: String,
    @JacksonXmlProperty(localName = "rating")
    val rating: Int,
    @JacksonXmlProperty(localName = "games")
    val games: Int,
    @JacksonXmlProperty(localName = "k")
    val k: Int,
    @JacksonXmlProperty(localName = "birthday")
    val birthday: Int,
    @JacksonXmlProperty(localName = "flag")
    val flag: String
)
