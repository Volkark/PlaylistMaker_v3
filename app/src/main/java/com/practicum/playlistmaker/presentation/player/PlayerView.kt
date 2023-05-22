package com.practicum.playlistmaker.presentation.player

interface PlayerView {
    fun playingState()
    fun pauseState()
    fun readyState()
    fun timing(newTime : String)
    fun heartState(state : Boolean)
}
