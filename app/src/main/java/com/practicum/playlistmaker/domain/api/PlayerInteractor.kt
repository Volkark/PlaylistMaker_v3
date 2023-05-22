package com.practicum.playlistmaker.domain.api

// Интерфейс плеера
interface PlayerInteractor {
    fun create(source : String, onSourceReady: () -> Unit, onSourceEnd: () -> Unit)
    fun start()
    fun stop()
    fun destroy()
    fun time() : String
}
