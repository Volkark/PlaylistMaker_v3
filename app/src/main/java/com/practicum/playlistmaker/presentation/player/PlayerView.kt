package com.practicum.playlistmaker.presentation.player

import com.practicum.playlistmaker.domain.models.Track

interface PlayerView {
    fun trackState(track : Track?)
    fun playingState()
    fun pauseState()
    fun readyState()
    fun timing(newTime : String)
    fun heartState(state : Boolean)
}
