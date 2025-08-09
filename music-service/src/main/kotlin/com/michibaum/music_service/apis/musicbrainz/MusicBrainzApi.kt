package com.michibaum.music_service.apis.musicbrainz

import com.michibaum.music_service.apis.ApiResult
import com.michibaum.music_service.apis.musicbrainz.dtos.ArtistResponse
import com.michibaum.music_service.apis.musicbrainz.dtos.ReleaseResponse

interface MusicBrainzApi {
    fun getArtistByMBID(mbId: String): ApiResult<ArtistResponse>
    fun getReleaseByMBID(mbId: String): ApiResult<ReleaseResponse>
}