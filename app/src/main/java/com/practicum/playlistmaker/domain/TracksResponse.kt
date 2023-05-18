package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.domain.Track

data class TracksResponse(
    val resultCount: Int,
    val results: ArrayList<Track>
)
