package com.practicum.playlistmaker.data.dto

data class PlayerDto(   val trackUrl : String,
                        val onTrackReady: () -> Unit,
                        val onTrackEnd: () -> Unit)
