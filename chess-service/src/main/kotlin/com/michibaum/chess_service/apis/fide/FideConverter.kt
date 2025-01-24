package com.michibaum.chess_service.apis.fide

import com.michibaum.chess_service.apis.dtos.AccountDto
import com.michibaum.chess_service.apis.dtos.FidePersonDto
import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.Gender
import org.springframework.stereotype.Component

@Component
class FideConverter {

    fun convertToAccount(fidePlayer: FidePlayer): AccountDto {
        return AccountDto(
            id = fidePlayer.fideid,
            url = "https://ratings.fide.com/profile/" + fidePlayer.fideid,
            username = fidePlayer.name,
            name = fidePlayer.name,
            platform = ChessPlatform.FIDE
        )
    }

    fun convertToPerson(fidePlayer: FidePlayer): FidePersonDto {
        val splitName = fidePlayer.name.split(", ")
        return FidePersonDto(
            firstname = splitName[1],
            lastname = splitName[0],
            fideId = fidePlayer.fideid,
            federation = fidePlayer.country,
            gender = convertGender(fidePlayer.sex)
        )
    }

    fun convertGender(g: String): Gender =
        when (g) {
            "M" -> Gender.MALE
            "F" -> Gender.FEMALE
            else -> Gender.UNKNOWN
        }

}