package com.practicum.playlistmaker.presentation.tracks

import android.content.Context
import com.practicum.playlistmaker.domain.models.Track

interface TrackAdapterView {
    fun updateHistory(track : Track)
    fun updateChoice(position : Int)
    fun startPlayerActivity()
}
