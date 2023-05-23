package com.practicum.playlistmaker.domain.api

import android.content.Context
import com.practicum.playlistmaker.data.dto.PlayerDto
import com.practicum.playlistmaker.domain.models.Track

interface PlayerRepository {
    fun getTrack() : Track?
    fun trackPrepare(playerDto : PlayerDto)
    fun trackStart()
    fun trackStop()
    fun getTrackPosition() : Int
    fun destroy()
}
