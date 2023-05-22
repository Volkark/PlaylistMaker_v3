package com.practicum.playlistmaker.presentation.tracks

import com.practicum.playlistmaker.domain.models.Track

// Состояния экрана поиска
enum class ScreenModes {
    SCR_DOWNLOAD,       // Загрузка
    SCR_EMPTY,          // Ничего не нашлось
    SCR_ERROR,          // Проблемы со связью
    SCR_READY,          // Данные готовы
    SCR_HISTORY         // История поиска
}

interface TracksView {
    fun hideKeyboard()
    fun setSearchScreenMode(screenMode : ScreenModes)
    fun tracksClear()
    fun addTracks(tracks : ArrayList<Track>)
    fun updateTracksList()
}
