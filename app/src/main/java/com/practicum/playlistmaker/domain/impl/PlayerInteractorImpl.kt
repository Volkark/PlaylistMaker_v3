package com.practicum.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.data.dto.PlayerDto
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.*

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

    override fun getTrack() : Track? {
        return repository.getTrack()
    }

    override fun create(playerDto : PlayerDto) {
        repository.trackPrepare(playerDto)
    }

    override fun start() {
        repository.trackStart()
    }

    override fun stop() {
        repository.trackStop()
    }

    override fun destroy() {
        repository.destroy()
    }

    override fun time() : String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(repository.getTrackPosition())
    }
}
