package com.practicum.playlistmaker.ui.tracks

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.storage.ChoiceStore
import com.practicum.playlistmaker.data.storage.SEARCH_HISTORY
import com.practicum.playlistmaker.data.storage.TracksStore
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.tracks.ScreenModes
import com.practicum.playlistmaker.presentation.tracks.TrackAdapterView
import com.practicum.playlistmaker.presentation.tracks.TracksView
import com.practicum.playlistmaker.ui.lastSearchText
import com.practicum.playlistmaker.ui.player.PlayerActivity
import com.practicum.playlistmaker.ui.searchText

class SearchActivity : AppCompatActivity(), TracksView, TrackAdapterView {

    private val tracksSearchPresenter =
        Creator.provideTracksSearchPresenter(this, this)

    companion object {
        // Константы для сохранения текущих результатов последнего запроса и строки поиска  и
        // результатов запроса и строки поиска до последней очистки для реализации поворота экрана
        private const val FOUND_TRACKS = "FOUND_TRACKS"
        private const val UPDATE_TRACKS = "UPDATE_TRACKS"
        private const val UPDATE_SEARCH = "UPDATE_SEARCH"
        private const val SEARCH_SCREEN_MODE = "SEARCH_SCREEN_MODE"

        const val HISTORY_SIZE = 10
    }

    // View-элементы Activity, используемые в разных методах класса SearchActivity (не в одном)
    // для предотвращения повторных вызовов findViewById при обращении к этим View-элементам
    private val searchEditText by lazy { findViewById<EditText>(R.id.inputEditText) }   // Текст запроса
    private val updateButton by lazy { findViewById<Button>(R.id.button_update) }      // Кнопка перезапроса
    private val recyclerTracks by lazy { findViewById<RecyclerView>(R.id.tracks_list) } // Результаты запроса

    // Переменные
    private var updateText: String =
        ""                            // Строка для сохранения строки поиска перед очисткой
    private val foundTracks: ArrayList<Track> = arrayListOf()      // Найденные треки
    private val historyTracks: ArrayList<Track> = arrayListOf()    // Треки истории поиска
    private val updateTracks: ArrayList<Track> = arrayListOf()     // Найденные треки, сохраненные перед перед очисткой
    private var screenState: ScreenModes = ScreenModes.SCR_READY   // Состояние экрана

    private val trackAdapter = TrackAdapter(foundTracks, this)
    private val historyAdapter = HistoryAdapter(historyTracks, this)

    // Сохранение данных, необходимых для реализации поворота экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            SEARCH_SCREEN_MODE,
            Gson().toJson(screenState)
        )  // Сохранение состояния экрана
        outState.putString(
            FOUND_TRACKS,
            Gson().toJson(foundTracks)
        )        // Сохранение найденных треков
        outState.putString(
            UPDATE_TRACKS,
            Gson().toJson(updateTracks)
        )      // Сохранение треков, бэкапнутых перед очисткой
        outState.putString(
            UPDATE_SEARCH,
            updateText
        )                       // Сохранение строки поиска, бэкапнутой перед очисткой
    }

    // Восстановление данных, необходимых для реализации поворота экрана
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        var jsonString = savedInstanceState.getString(
            SEARCH_SCREEN_MODE,
            ""
        )   // Восстановление состояния экрана
        if (jsonString != "") {
            screenState = Gson().fromJson<ScreenModes>(
                jsonString, object : TypeToken<ScreenModes>() {}.type
            )
        }

        jsonString = savedInstanceState.getString(
            FOUND_TRACKS,
            ""
        )             // Восстановление списка треков
        if (jsonString != "") {
            foundTracks.clear()
            val tracks = Gson().fromJson<ArrayList<Track>>(
                jsonString, object : TypeToken<ArrayList<Track>>() {}.type
            )
            if (tracks != null)
                foundTracks.addAll(tracks)
        }

        jsonString = savedInstanceState.getString(
            UPDATE_TRACKS,
            ""
        )           // Восстановление списка треков бэкапнутых перед очисткой
        if (jsonString != "") {
            updateTracks.clear()
            val tracks = Gson().fromJson<ArrayList<Track>>(
                jsonString, object : TypeToken<ArrayList<Track>>() {}.type
            )
            if (tracks != null)
                updateTracks.addAll(tracks)
        }
        updateText = savedInstanceState.getString(
            UPDATE_SEARCH,
            ""
        )            // Восстановление строки поиска бэкапнутой перед очисткой
    }

    override fun onResume() {
        super.onResume()
        // Строковая переменная для поиска восстанавливается из inputEditText после того,
        //  как значения inputEditText были восстановлены дефолтными методами
        searchText = searchEditText.text.toString()
        lastSearchText = searchText
        // Установка состояния экрана после восстановления переменной состояния экрана или по-умолчанию, если первый вход
        setSearchScreenMode(screenState)
    }

    // Создание и настройка экрана поиска
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Реализация нажатия кнопки <Назад> экрана <Поиск> для перехода на главный экран
        findViewById<Button>(R.id.return_from_search)
            .setOnClickListener { finish() }

        // Считывание истории поиска из Shared Preferences
        val tracksStore = TracksStore(this, SEARCH_HISTORY)
        if (tracksStore.wasSaved()) {
            historyTracks.clear()
            historyTracks.addAll(tracksStore.getStored()!!)
        }

        // Инициализация RecyclerView для просмотра результато поиска
        recyclerTracks.layoutManager = LinearLayoutManager(this)
        recyclerTracks.adapter = trackAdapter

        // Объявление и инициализация RecyclerView для просмотра истории
        val recyclerHistory = findViewById<RecyclerView>(R.id.history_list)
        recyclerHistory.layoutManager = LinearLayoutManager(this)
        recyclerHistory.adapter = historyAdapter

        // Реализация нажатия кнопки "Очистить поисковый запрос" (х)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            // Сохранить текст поискового запроса перед очисткой
            updateText = searchEditText.text.toString()
            // Сохранить список треков последнего поиска перед очисткоц
            updateTracks.clear()
            updateTracks.addAll(trackAdapter.tracks)
            // Очистить поисковый запрос
            searchEditText.setText("")
            //Очистить список треков
            tracksClear()
            // Скрыть клавиатуру
            hideKeyboard()
            // Перерисовать список на экране
            updateTracksList()
        }

        // Реализация редактирования поискового запроса
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {                        // При пустой строке ввода
                    clearButton.visibility = View.GONE              // Скрыть кнопку "Очистить"
                    if (historyTracks.isNullOrEmpty())              // Если история поиска пустая
                        setSearchScreenMode(ScreenModes.SCR_READY)      // Переключиться на экран последнего поиска
                    else {                                          // Если история поиска не пустая
                        if (screenState != ScreenModes.SCR_HISTORY) {   // Отобразить историю поиска
                            historyAdapter.notifyDataSetChanged()
                            setSearchScreenMode(ScreenModes.SCR_HISTORY)
                        }
                    }
                    // Отключить автоматический запуск поиска через 2 сек после последнего изменения строки ввода (т.к. строка пустая)
                    tracksSearchPresenter.clearSearchStartTimeOut()
                } else {                                              // При не пустой строке ввода
                    clearButton.visibility =
                        View.VISIBLE               // Показать кнопку "Очистить"
                    if (screenState == ScreenModes.SCR_HISTORY) {       // Если состояние экрана "История поиска"
                        updateTracksList()
                        setSearchScreenMode(ScreenModes.SCR_READY)          // Переключиться на экран последнего поиска
                    }
                    // Перезапустить автоматический запуск поиска через 2 сек после последнего изменения строки ввода
                    tracksSearchPresenter.resetSearchStartTimeOut()
                }
                searchText =
                    s.toString()                           // Сохранить строку ввода в переменной
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        // Реализация переключения на экран истории при получении фокуса строки редактирования поискового запроса
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            setSearchScreenMode(
                if (hasFocus && searchEditText.text.isNullOrBlank() && !historyTracks.isNullOrEmpty())
                    ScreenModes.SCR_HISTORY
                else
                    ScreenModes.SCR_READY
            )
        }

        // Реализация нажатия кнопки c лупой с левой стороны строки поиска
        findViewById<ImageView>(R.id.loupeIcon)
            .setOnClickListener {
                if (searchText.isNullOrBlank()) {                   // При пустой строке поиска
                    searchEditText.setText(updateText)                  // Восстановить последнюю строку поиска
                    lastSearchText = updateText
                    // Восстановить список треков последнего запроса
                    tracksClear()
                    addTracks(updateTracks)
                    updateTracksList()
                    setSearchScreenMode(ScreenModes.SCR_READY)
                } else {                                              // При не пустой строке поиска
                    lastSearchText = ""                                 // Сбросить строку последнего запроса
                    tracksSearchPresenter.searchRequest()                                         // Запустить новый запрос
                }
            }

        // Реализация нажатия кнопки "Обновить" после неудачного поиска
        updateButton.setOnClickListener {
            lastSearchText = ""
            tracksSearchPresenter.searchRequest()                                         // Запустить новый запрос
        }

        // Реализация нажатия кнопки Done клавиатуры - запуск нового запроса
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                lastSearchText =
                    ""                                 // Сбросить строку последнего запроса
                tracksSearchPresenter.searchRequest()                                         // Запустить новый запрос
            }
            false
        }

        // Реализация нажатия кнопки "Очистить историю"
        findViewById<Button>(R.id.button_clear_history)
            .setOnClickListener {
                historyAdapter.history.clear()
                TracksStore(this, SEARCH_HISTORY)
                    .save(historyAdapter.history)
                updateTracksList()
                setSearchScreenMode(ScreenModes.SCR_READY)
            }
    }

    // Скрыть клавиатуру
    override fun hideKeyboard() {
        (getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(
                searchEditText
                    .windowToken, 0
            )
    }

    // Установка экрана поиска в заданное состояние
    override fun setSearchScreenMode(screenMode: ScreenModes) {

        // UI-элементы, обращение к которым в этом методе делается несколько раз
        val historyLayout =
            findViewById<LinearLayout>(R.id.layout_on_history)  // Панель истории поиска
        val warningLayout =
            findViewById<LinearLayout>(R.id.layout_on_error)    // Панель предупреждений
        val warningText =
            findViewById<TextView>(R.id.text_on_error)            // Текст предупреждения

        // Переменные задающие видимость UI-элементов
        var downloadView = View.GONE
        var progressBarView = View.GONE
        var emptyErrorView = View.GONE
        var linkErrorView = View.GONE
        var buttonView = View.GONE

        screenState = screenMode                            // Сохранение состояние экрана

        when (screenMode) {
            ScreenModes.SCR_DOWNLOAD -> {                       // Загрузка
                warningText.setText(R.string.download_message)
                downloadView = View.INVISIBLE
                progressBarView = View.VISIBLE
            }
            ScreenModes.SCR_EMPTY -> {                          // Ничего не нашлось
                warningText.setText(R.string.empty_error)
                emptyErrorView = View.VISIBLE
            }
            ScreenModes.SCR_ERROR -> {                          // Проблемы со связью
                warningText.setText(R.string.net_error)
                linkErrorView = View.VISIBLE
                buttonView = View.VISIBLE
            }
            ScreenModes.SCR_HISTORY -> {                        // История поиска
                warningLayout.visibility = View.GONE                // Скрытие панели предупреждений
                recyclerTracks.visibility = View.GONE               // Скрытие списка треков
                historyLayout.visibility =
                    View.VISIBLE             // Отображение панели истории поиска
                return
            }
            ScreenModes.SCR_READY -> {                          // Данные готовы
                warningLayout.visibility = View.GONE                // Скрытие панели предупреждений
                historyLayout.visibility = View.GONE               // Скрытие панели истории поиска
                recyclerTracks.visibility = View.VISIBLE            // Отображение списка треков
                return
            }
        }

        // findViewById для этих UI-элементов вызывается только здесь
        findViewById<ProgressBar>(R.id.progressBarSearch).visibility =
            progressBarView  // Установка выбранной видимости прогресс-бара загрузки
        findViewById<ImageView>(R.id.image_download).visibility =
            downloadView          // Установка выбранной видимости картинки "Заграузка"
        findViewById<ImageView>(R.id.image_empty_error).visibility =
            emptyErrorView     // Установка выбранной видимости картинки "Пусто"
        findViewById<ImageView>(R.id.image_link_error).visibility =
            linkErrorView       // Установка выбранной видимости картинки "Нет связи"

        // Результаты вызова findViewById для этих UI-элементов берутся из переменных
        recyclerTracks.visibility =
            View.GONE                                           // Скрытие списка треков
        historyLayout.visibility =
            View.GONE                                            // Скрытие панели истории поиска
        updateButton.visibility =
            buttonView                                            // Установка выбранной видимости кнопки "Обновить"
        warningLayout.visibility =
            View.VISIBLE                                         // Отображение настроенной панели предупреждений
    }

    override fun tracksClear() {
        trackAdapter.tracks.clear()
    }

    override fun addTracks(tracks : ArrayList<Track>) {
        trackAdapter.tracks.addAll(tracks)
    }

    override fun updateTracksList() {
        trackAdapter.notifyDataSetChanged()
    }

    override fun updateChoice(position : Int) {
        // Сохранение позиции выбранного трека в Shared Preferеnces
        ChoiceStore(this).save(position)
    }

    override fun updateHistory(track : Track) {
        val history = historyAdapter.history
        if (history.contains(track)) history.remove(track)
        else if (history.size == HISTORY_SIZE) history.removeAt(HISTORY_SIZE - 1)
        history.add(0, track)
        // Сохранение истории поиска в Shared Prefernces
        TracksStore(this, SEARCH_HISTORY)
            .save(history)
        // Сохранение 0, как позиции выбранного трека в Shared Preferеnces
        updateChoice(0)
    }

    override fun startPlayerActivity() {
        startActivity(
            Intent(this, PlayerActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        tracksSearchPresenter.onDestroy()
    }
}
