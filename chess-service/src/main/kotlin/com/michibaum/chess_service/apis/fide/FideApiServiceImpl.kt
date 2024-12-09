package com.michibaum.chess_service.apis.fide

import com.michibaum.chess_service.apis.dtos.AccountDto
import com.michibaum.chess_service.apis.dtos.FidePersonDto
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class FideApiServiceImpl(
    private val fideImporter: FideImporter,
    private val fideConverter: FideConverter
): FideApiService {

    override fun getAccounts(inputStream: InputStream): List<AccountDto> {
        val imports = fideImporter.import(inputStream)
        val filtered = filterAccounts(imports)
        return filtered.map(fideConverter::convertToAccount)
    }

    override fun getPersons(inputStream: InputStream): List<FidePersonDto> {
        val imports = fideImporter.import(inputStream)
        val filtered = filterPersons(imports)
        return filtered.map(fideConverter::convertToPerson)
    }

    private fun filterAccounts(fidePlayer: List<FidePlayer>): List<FidePlayer> = fidePlayer.asSequence()
            .filter { it.rating > 2000 }
            .filter { it.fideid.isNotBlank() }
            .filter { it.name.isNotBlank() }
            .toList()

    private fun filterPersons(fidePlayer: List<FidePlayer>): List<FidePlayer> =
        fidePlayer.asSequence()
            .filter { it.rating > 2000 }
            .filter { it.fideid.isNotBlank() }
            .filter { it.name.isNotBlank() }
            .filter { it.country.isNotBlank() }
            .filter { it.sex.isNotBlank() }
            .filter { it.name.contains(", ") } // split into firstname and lastname
            .filter { it.sex == "F" || it.sex == "M" }
            .toList()

}