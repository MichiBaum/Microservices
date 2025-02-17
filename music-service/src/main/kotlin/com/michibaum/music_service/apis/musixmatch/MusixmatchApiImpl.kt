package com.michibaum.music_service.apis.musixmatch

import com.michibaum.music_service.apis.musixmatch.dtos.*
import com.michibaum.music_service.config.properties.MusixmatchProperties
import org.springframework.stereotype.Component

@Component
class MusixmatchApiImpl(
    val musixmatchProperties: MusixmatchProperties
): MusixmatchApi, AbstractMusixmatchApiClient() { // https://docs.musixmatch.com/lyrics-api/introduction

    fun getTrackByIsrc(isrc: String){
        client.get()
            .uri("/track.get")
            .attributes {
                it["track_isrc"] = isrc
                it["apikey"] = musixmatchProperties.apiKey
            }
            .retrieve()
            .body(TrackResponse::class.java)
    }

    fun getArtist(id: String){
        client.get()
            .uri("/artist.get")
            .attributes {
                it["artist_id"] = id
                it["apikey"] = musixmatchProperties.apiKey
            }
            .retrieve()
            .body(ArtistResponse::class.java)
    }

    fun getAlbum(id: String){
        client.get()
            .uri("/album.get")
            .attributes {
                it["album_id"] = id
                it["apikey"] = musixmatchProperties.apiKey
            }
            .retrieve()
            .body(AlbumResponse::class.java)
    }

    fun getTopTracks(country: String, chartType: ChartType){ // https://docs.musixmatch.com/lyrics-api/charts/chart-tracks-get
        client.get()
            .uri("/chart.tracks.get")
            .attributes {
                it["country"] = country
                it["chart_name"] = chartType.name
                it["apikey"] = musixmatchProperties.apiKey
            }
            .retrieve()
            .body(TopTracksResponse::class.java)
    }

    fun getTopArtists(country: String){
        client.get()
            .uri("/chart.artists.get")
            .attributes {
                it["country"] = country
                it["apikey"] = musixmatchProperties.apiKey
            }
            .retrieve()
            .body(TopArtistsResponse::class.java)
    }

}