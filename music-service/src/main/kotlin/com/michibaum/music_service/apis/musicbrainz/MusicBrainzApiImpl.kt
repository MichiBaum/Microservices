package com.michibaum.music_service.apis.musicbrainz

import com.michibaum.music_service.apis.ApiResult
import com.michibaum.music_service.apis.musicbrainz.dtos.ArtistResponse
import com.michibaum.music_service.apis.musicbrainz.dtos.ReleaseResponse
import com.michibaum.music_service.config.properties.ApisProperties
import org.springframework.web.client.RestClient

class MusicBrainzApiImpl(
    restClientBuilder: RestClient.Builder, apisProperties: ApisProperties
): MusicBrainzApi, AbstractMusicBrainzApiClient(
    restClientBuilder, apisProperties
) {

    override fun getArtistByMBID(mbId: String): ApiResult<ArtistResponse> =
        executeApiCall {
            client.get()
                .uri("/artist/$mbId")
                .attributes {
                    it["inc"] = "release-groups"
                }
                .retrieve()
                .body(ArtistResponse::class.java)
        }

    override fun getReleaseByMBID(mbId: String): ApiResult<ReleaseResponse> =
        executeApiCall {
            client.get()
                .uri("/release/$mbId")
                .retrieve()
                .body(ReleaseResponse::class.java)
        }

    override fun getReleaseGroupByMBID(mbId: String): ApiResult<ArtistResponse> =
        executeApiCall {
            client.get()
                .uri("/release-group/$mbId")
                .attributes { 
                    it["inc"] = "artists+releases"
                }
                .retrieve()
                .body(ArtistResponse::class.java)
        }
    
}