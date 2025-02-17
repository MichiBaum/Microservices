package com.michibaum.music_service.apis.musixmatch

import com.michibaum.music_service.apis.musixmatch.dtos.*
import com.michibaum.music_service.config.properties.ApisProperties
import com.michibaum.music_service.config.properties.MusixmatchProperties
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class MusixmatchApiImpl(
    restClientBuilder: RestClient.Builder,
    private val musixmatchProperties: MusixmatchProperties,
    apisProperties: ApisProperties
): MusixmatchApi, AbstractMusixmatchApiClient(restClientBuilder, apisProperties) { // https://docs.musixmatch.com/lyrics-api/introduction

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

    fun getAlbumTracks(id: String){
        client.get()
            .uri("/album.tracks.get")
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

    fun searchArtist(artistName: String){
        client.get()
            .uri("/artist.search")
            .attributes {
                it["q_artist"] = artistName
                it["apikey"] = musixmatchProperties.apiKey
            }
            .retrieve()
            .body(SearchArtistResponse::class.java)
    }

}