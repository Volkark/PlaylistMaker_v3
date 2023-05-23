package com.practicum.playlistmaker.data.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.*

class PlayerInteractorImpl() : PlayerInteractor {
    private val mediaPlayer = MediaPlayer()

    override fun create(trackUrl : String, onTrackReady: () -> Unit, onTrackEnd: () -> Unit) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onTrackReady()
        }
        mediaPlayer.setOnCompletionListener {
            onTrackEnd()
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun stop() {
        mediaPlayer.pause()
    }

    override fun destroy() {
        mediaPlayer.release()
    }

    override fun time() : String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }
}
