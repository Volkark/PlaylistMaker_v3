package com.practicum.playlistmaker.presentation.player

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.impl.CoverLoaderImpl
import com.practicum.playlistmaker.presentation.tracks.TracksView
import com.practicum.playlistmaker.ui.lastTrack
import com.practicum.playlistmaker.ui.player.PlayerActivity
import com.practicum.playlistmaker.util.Creator

// Состояния плеера
private enum class PlayerModes {
    PLR_NO_READY,       // Трек не готов к воспроизведению
    PLR_PLAYING,        // Трек проигрывается
    PLR_PAUSE,          // Трек остановлен
}

class PlayerPresenter(private val view: PlayerView,
                      private val context: Context) {

    private val playerInteractor = Creator.providePlayerInteractor()

    private var playerState: PlayerModes = PlayerModes.PLR_NO_READY
    private var heartState : Boolean = false

    private val handler = Handler(Looper.getMainLooper())
    private val playTimeEvent = Runnable {
        view.timing(playerInteractor.time())
        setPlayTimeEvent()
    }

    private fun setPlayTimeEvent() {
        handler.postDelayed(playTimeEvent, 100)
    }

    private fun removePlayTimeEvent() {
        handler.removeCallbacks(playTimeEvent)
    }

    fun create(source: String) {
        playerInteractor.create(source, ::readyForStart, ::endOfTrack)
    }

    fun destroy() {
        playerInteractor.destroy()
        removePlayTimeEvent()
    }

    fun start() {
        if (playerState == PlayerModes.PLR_PAUSE) {
            playerState = PlayerModes.PLR_PLAYING
            view.playingState()
            playerInteractor.start()
            setPlayTimeEvent()
        }
    }

    fun stop() {
        if (playerState == PlayerModes.PLR_PLAYING) {
            playerState = PlayerModes.PLR_PAUSE
            view.pauseState()
            playerInteractor.stop()
            removePlayTimeEvent()
        }
    }

    private fun readyForStart() {
        playerState = PlayerModes.PLR_PAUSE
        view.readyState()
    }

    private fun endOfTrack() {
        stop()
        view.timing("0:00")
        // Если сердце выбрано, то запустить снова на проигрывание
        if (heartState)
            start()
    }

    fun heart(state : Boolean) {
        heartState = state
        view.heartState(state)
    }

    fun lostFocus() {
        if (!heartState)
            stop()
    }
}
