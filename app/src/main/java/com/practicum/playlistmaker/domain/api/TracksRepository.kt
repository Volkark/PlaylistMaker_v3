package com.practicum.playlistmaker.domain.api

import android.content.Context
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.util.Resource

interface TracksRepository {
    fun searchTracks(expression: String) : Resource<ArrayList<Track>>
    fun storeHistory(context : Context, history : ArrayList<Track>)
    fun storePosition(context : Context, position : Int)
    fun getHistory(context : Context) : ArrayList<Track>
}
