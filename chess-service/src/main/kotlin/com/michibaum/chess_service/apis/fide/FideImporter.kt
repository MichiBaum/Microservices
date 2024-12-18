package com.michibaum.chess_service.apis.fide

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.stereotype.Component
import java.io.InputStream


@Component
class FideImporter {

    fun import(inputStream: InputStream): List<FidePlayer> {
        val xmlMapper = XmlMapper()
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        val typeReference = object : TypeReference<List<FidePlayer>>() {}
        val fidePlayerList: List<FidePlayer> = xmlMapper.readValue(inputStream, typeReference)
        return fidePlayerList
    }

    fun importJob(){
        // TODO create night job
        //  download zip from https://ratings.fide.com/download/standard_rating_list_xml.zip
        //  unzip it and save if not same as downloaded before (hash?)
        //  start import job
    }

}