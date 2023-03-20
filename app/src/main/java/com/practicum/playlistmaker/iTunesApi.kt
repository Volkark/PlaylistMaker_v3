package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    // Пример запроса с https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/iTuneSearchAPI/SearchExamples.html
    // https://itunes.apple.com/search?term=jack+johnson
    @GET("search")
    fun searchTracks(@Query("term") namePiece: String): Call<TracksResponse>
}
