package com.michibaum.chess_service.app.person.fide_import

import com.michibaum.chess_service.apis.fide.FideImporter
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import java.io.InputStream

class FideImporterUT {

    @Test
    fun test(){
        val fideImporter = FideImporter()
        val resource: Resource = ClassPathResource("fide_import/2024-dez.xml")
        val inputStream: InputStream = resource.inputStream

        // THEN
        val result = fideImporter.import(inputStream)

        println(">Size: ${result.size}<")

        val withoutId = result.filter { it.fideid.isBlank() }
        println(">Without fideId: ${withoutId.size}<")

        val withoutNamen = result.filter { it.name.isBlank() }
        println(">Without name: ${withoutNamen.size}<")

        val withoutCountry = result.filter { it.country.isBlank() }
        println(">Without country: ${withoutCountry.size}<")

        val withoutSex = result.filter { it.sex.isBlank() }
        println(">Without sex: ${withoutSex.size}<")

        val withRating0 = result.filter { it.rating == 0 }
        println(">Without rating 0: ${withRating0.size}<")

        val withRatingBelow2000 = result.filter { it.rating < 2000 }
        println(">Without rating < 2000: ${withRatingBelow2000.size}<")

        val withZeroGames = result.filter { it.games == 0 }
        println(">Without games 0: ${withZeroGames.size}<")
    }

}