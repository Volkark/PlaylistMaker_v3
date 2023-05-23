package com.practicum.playlistmaker.domain.api

import android.content.Context
import com.practicum.playlistmaker.domain.models.Track

interface PlayerRepository {
    fun selectedTrack(context : Context) : Track?
}
