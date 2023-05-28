package com.practicum.playlistmaker.domain.api

import android.content.Context
import com.practicum.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    interface TracksConsumer {
        fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?)
    }

    fun getTracksHistory(context : Context) : ArrayList<Track>
    fun clearTracksHistory(context : Context, history: ArrayList<Track>)
    fun updateTracksHistory(context : Context, history: ArrayList<Track>, track: Track)
    fun updatePosition(context : Context, position : Int)
}
