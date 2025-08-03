package com.michibaum.chess_service.apis.fide

import com.michibaum.chess_service.apis.dtos.AccountDto
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

    private fun filterAccounts(fidePlayer: List<FidePlayer>): List<FidePlayer> = fidePlayer.asSequence()
            .filter { it.rating > 2300 }
            .filter {
                val grandmaster = it.title == "g" || it.title == "wg"
                val internationalMaster = it.title == "m" || it.title == "wm"
                grandmaster || internationalMaster
            }
            .filter { it.fideid.isNotBlank() }
            .filter { it.name.isNotBlank() }
            .toList()

}