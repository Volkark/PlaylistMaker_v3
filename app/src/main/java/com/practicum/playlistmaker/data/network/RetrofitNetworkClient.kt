package com.practicum.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksRequest
import com.practicum.playlistmaker.data.iTunesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val context: Context) : NetworkClient {

    private val iTuneUrl = "https://itunes.apple.com"               // url для поиска треков

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTuneUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTuneService = retrofit.create(iTunesApi::class.java)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun doRequest(dto: Any): Response {
        if (isConnected()) {
            if (dto is TracksRequest) {
                val responce = iTuneService.searchTracks(dto.expression).execute()
                val body = responce.body() ?: Response()
                return body.apply { resultCode = responce.code() }
            } else {
                return Response().apply { resultCode = 400 }
            }
        }
        else {
            return Response().apply { resultCode = -1 }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
