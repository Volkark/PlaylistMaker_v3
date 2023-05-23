package com.practicum.playlistmaker.presentation.tracks

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.lastSearchText
import com.practicum.playlistmaker.ui.searchText

// Поворот экрана: onPause -> onStop -> onSaveInstanceState -> onDestroy ->
// -> onCreate -> onStart -> onRestoreInstanceState -> onResume

class TracksSearchPresenter(private val view: TracksView,
                            private val context: Context) {

    private val tracksInteractor = Creator.provideTracksInteractor(context)

    companion object {
        // Задержка начала поиска после окончания редактирования строки ввода
        private const val SEARCH_START_DELAY = 2000L
    }

    // Организация потока для автоматического запуска поиска по таймеру
    private val handler = Handler(Looper.getMainLooper())

    private val searchStart = Runnable {
        searchRequest()
    }

    fun resetSearchStartTimeOut() {
        clearSearchStartTimeOut()
        handler.postDelayed(searchStart, SEARCH_START_DELAY)
    }

    fun clearSearchStartTimeOut() {
        handler.removeCallbacks(searchStart)
    }

    fun onDestroy() {
        clearSearchStartTimeOut()
    }

    // Организация запроса
    fun searchRequest() {

        if (searchText == lastSearchText)
            return

        view.hideKeyboard()

        if (!searchText.isNullOrBlank()) {
            lastSearchText = searchText
            view.setSearchScreenMode(ScreenModes.SCR_DOWNLOAD)                       // Данные загружаются
            tracksInteractor.searchTracks(searchText.toString(), object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?) {
                    handler.post {
                        view.tracksClear()
                        if ((errorMessage != null) || (foundTracks == null))
                            view.setSearchScreenMode(ScreenModes.SCR_ERROR)          // Ошибка доступа к сети
                        else {
                            if (foundTracks.isEmpty())
                                view.setSearchScreenMode(ScreenModes.SCR_EMPTY)      // Сервер не вернул ни одного трека
                            else {
                                view.addTracks(foundTracks)
                                view.setSearchScreenMode(ScreenModes.SCR_READY)      // Данные готовы
                            }
                        }
                        view.updateTracksList()
                    }
                }
            })
        }
    }
}
