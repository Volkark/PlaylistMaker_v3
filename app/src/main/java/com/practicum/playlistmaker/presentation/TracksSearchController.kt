package com.practicum.playlistmaker.presentation

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.tracks.HistoryAdapter
import com.practicum.playlistmaker.ui.tracks.SearchActivity
import com.practicum.playlistmaker.ui.tracks.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TracksSearchController(private val activity: Activity,
                             private val adapter: TrackAdapter
) {
    private val tracksInteractor = Creator.provideTracksInteractor()

    companion object {
        private const val SEARCH_START_DELAY = 2000L
    }

    private var screenState: SearchActivity.ScreenModes =
        SearchActivity.ScreenModes.SCR_READY   // Состояние экрана
    private var searchText: String = ""                            // Набранная строка поиска
    private var lastSearchText: String = ""

    private val recyclerTracks by lazy { activity.findViewById<RecyclerView>(R.id.tracks_list) }
    private val searchEditText by lazy { activity.findViewById<EditText>(R.id.inputEditText) }
    private val clearButton by lazy { activity.findViewById<ImageView>(R.id.clearIcon) }
    private val recyclerHistory by lazy { activity.findViewById<RecyclerView>(R.id.history_list) }

    //private lateinit var placeholderMessage: TextView
    //private lateinit var progressBar: ProgressBar

    private val tracks = arrayListOf<Track>()

    private val historyTracks = arrayListOf<Track>()

    private val handler = Handler(Looper.getMainLooper())

    private val searchStart = Runnable { searchRequest() }

    fun onCreate() {
        //placeholderMessage = activity.findViewById(R.id.placeholderMessage)
        //progressBar = activity.findViewById(R.id.progressBar)

        adapter.tracks = tracks

        // Инициализация RecyclerView для просмотра результато поиска
        recyclerTracks.layoutManager = LinearLayoutManager(activity)
        recyclerTracks.adapter = adapter // TrackAdapter(foundTracks, historyTracks)

        // Реализация редактирования поискового запроса
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {                        // При пустой строке ввода
                    clearButton.visibility = View.GONE              // Скрыть кнопку "Очистить"
                    if (historyTracks.isNullOrEmpty())              // Если история поиска пустая
                        setSearchScreenMode(SearchActivity.ScreenModes.SCR_READY)      // Переключиться на экран последнего поиска
                    else {                                          // Если история поиска не пустая
                        if (screenState != SearchActivity.ScreenModes.SCR_HISTORY) {   // Отобразить историю поиска
                            (recyclerHistory.adapter as HistoryAdapter).notifyDataSetChanged()
                            setSearchScreenMode(SearchActivity.ScreenModes.SCR_HISTORY)
                        }
                    }
                    // Отключить автоматический запуск поиска через 2 сек после последнего изменения строки ввода (т.к. строка пустая)
                    clearSearchStartTimeOut()
                } else {                                              // При не пустой строке ввода
                    clearButton.visibility =
                        View.VISIBLE               // Показать кнопку "Очистить"
                    if (screenState == SearchActivity.ScreenModes.SCR_HISTORY) {       // Если состояние экрана "История поиска"
                        (recyclerTracks.adapter as TrackAdapter).notifyDataSetChanged()
                        setSearchScreenMode(SearchActivity.ScreenModes.SCR_READY)          // Переключиться на экран последнего поиска
                    }
                    // Перезапустить автоматический запуск поиска через 2 сек после последнего изменения строки ввода
                    resetSearchStartTimeOut()
                }
                searchText =
                    s.toString()                           // Сохранить строку ввода в переменной
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun setSearchScreenMode(scrReady: SearchActivity.ScreenModes) {

    }

    fun onDestroy() {
        clearSearchStartTimeOut()
    }

    private fun resetSearchStartTimeOut() {
        clearSearchStartTimeOut()
        handler.postDelayed(searchStart, SEARCH_START_DELAY)
    }

    private fun clearSearchStartTimeOut() {
        handler.removeCallbacks(searchStart)
    }

    // Скрыть клавиатуру
    private fun hideKeyboard() {
        //    (getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as? InputMethodManager)
        //        ?.hideSoftInputFromWindow(searchEditText
        //            .windowToken,0)
    }

    // Организация запроса
    private fun searchRequest() {

        if (searchText == lastSearchText)
            return

        hideKeyboard()

        if (!searchText.isNullOrBlank()) {
            lastSearchText = searchText
            setSearchScreenMode(SearchActivity.ScreenModes.SCR_DOWNLOAD)

            tracksInteractor.searchTracks(
                searchText.toString(),
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: ArrayList<Track>) {
                        handler.post {
                            tracks.clear()
                            tracks.addAll(foundTracks)
                            adapter.notifyDataSetChanged()
                            if (tracks.isEmpty()) {
                                setSearchScreenMode(SearchActivity.ScreenModes.SCR_EMPTY)
                            } else {
                                setSearchScreenMode(SearchActivity.ScreenModes.SCR_READY)
                            }
                        }
                    }
                })
        }
    }
}