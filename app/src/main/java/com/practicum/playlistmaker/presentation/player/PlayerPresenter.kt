package com.practicum.playlistmaker.presentation.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.data.dto.PlayerDto
import com.practicum.playlistmaker.util.Creator

// Состояния плеера
private enum class PlayerModes {
    PLR_NO_READY,       // Трек не готов к воспроизведению
    PLR_PLAYING,        // Трек проигрывается
    PLR_PAUSE,          // Трек остановлен
}

class PlayerPresenter(private val view: PlayerView,
                      private val context: Context) {

    private val playerInteractor = Creator.providePlayerInteractor(context)

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

    fun create() {
        val track = playerInteractor.getTrack()
        if (track != null) {
            val trackUrl: String = track.previewUrl
            if (trackUrl.isNotBlank() && trackUrl.length >= 10)
                playerInteractor.create(PlayerDto(trackUrl, ::readyForStart, ::endOfTrack))
        }
        view.trackState(track)
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
