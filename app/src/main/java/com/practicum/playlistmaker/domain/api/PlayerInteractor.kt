package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.dto.PlayerDto
import com.practicum.playlistmaker.domain.models.Track

// Интерфейс плеера
interface PlayerInteractor {
    fun getTrack() : Track?
    fun create(playerDto : PlayerDto)
    fun start()
    fun stop()
    fun destroy()
    fun time() : String
}
