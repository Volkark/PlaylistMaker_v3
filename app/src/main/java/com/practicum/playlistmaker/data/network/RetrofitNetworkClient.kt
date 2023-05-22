package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksRequest
import com.practicum.playlistmaker.data.iTunesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val iTuneUrl = "https://itunes.apple.com"               // url для поиска треков

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTuneUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTuneService = retrofit.create(iTunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TracksRequest) {
            val resp = iTuneService.searchTracks(dto.expression).execute()

            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}
