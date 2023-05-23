package com.practicum.playlistmaker.data.impl

import android.content.Context
import android.media.MediaPlayer
import com.practicum.playlistmaker.data.dto.PlayerDto
import com.practicum.playlistmaker.data.storage.ChoiceStore
import com.practicum.playlistmaker.data.storage.SEARCH_HISTORY
import com.practicum.playlistmaker.data.storage.TracksStore
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.models.Track

class PlayerRepositotyImpl(private val context : Context) : PlayerRepository {
    private val mediaPlayer = MediaPlayer()
    private var track: Track? = null

    override fun getTrack(): Track? {
        var position = 0
        val choiceStore = ChoiceStore(context)
        if (choiceStore.wasSaved())
            position = choiceStore.getStored()!!
        val tracksStore = TracksStore(context, SEARCH_HISTORY)
        if (tracksStore.wasSaved())
            track = tracksStore.getStored()!!.get(position)
        return track
    }

    override fun trackPrepare(playerDto : PlayerDto) {
        mediaPlayer.setDataSource(playerDto.trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerDto.onTrackReady()
        }
        mediaPlayer.setOnCompletionListener {
            playerDto.onTrackEnd()
        }
    }

    override fun getTrackPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun destroy() {
        mediaPlayer.release()
    }

    override fun trackStart() {
        mediaPlayer.start()
    }

    override fun trackStop() {
        mediaPlayer.pause()
    }
}
