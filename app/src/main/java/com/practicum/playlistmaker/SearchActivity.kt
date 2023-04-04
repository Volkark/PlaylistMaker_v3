package com.practicum.playlistmaker

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    // Поворот экрана: onPause -> onStop -> onSaveInstanceState -> onDestroy ->
    // -> onCreate -> onStart -> onRestoreInstanceState -> onResume

    // Состояния экрана поиска
    enum class ScreenModes {
        SCR_DOWNLOAD,       // Загрузка
        SCR_EMPTY,          // Ничего не нашлось
        SCR_ERROR,          // Проблемы со связью
        SCR_READY,          // Данные готовы
        SCR_HISTORY         // История поиска
    }

    // Переменные для сохранения текущих результатов последнего запроса и строки поиска  и
    // результатов запроса и строки поиска до последней очистки для реализации поворота экрана
    companion object {
        const val FOUND_TRACKS = "FOUND_TRACKS"
        const val UPDATE_TRACKS = "UPDATE_TRACKS"
        const val UPDATE_SEARCH = "UPDATE_SEARCH"
        const val SEARCH_SCREEN_MODE = "SEARCH_SCREEN_MODE"
    }

/*
    Для будущего использования с инициализацией в OnCreate
    для предотвращения вызова findViewById при обращении к UI-элементам
    в методах класса SearchActivity:

    private lateinit var updateButton   : Button
    private lateinit var warningImage   : ImageView
    private lateinit var warningText    : TextView
    private lateinit var warningLayout  : LinearLayout
    private lateinit var tarcksList     : RecyclerView
*/

    // Переменные
    private var searchText : String = ""                            // Набранная строка поиска
    private var updateText : String = ""                            // Строка для сохранения строки поиска перед очисткой
    private val foundTracks : ArrayList<Track> = arrayListOf()      // Найденные треки
    private val historyTracks : ArrayList<Track> = arrayListOf()    // Треки истории поиска
    private val updateTracks : ArrayList<Track> = arrayListOf()     // Найденные треки, сохраненные перед перед очисткой
    private var screenState : ScreenModes = ScreenModes.SCR_READY   // Состояние экрана

    private val iTuneUrl = "https://itunes.apple.com"               // url для поиска треков

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTuneUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTuneService = retrofit.create(iTunesApi::class.java)

    // Сохранение данных, необходимых для реализации поворота экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_SCREEN_MODE, Gson().toJson(screenState))  // Сохранение состояния экрана
        outState.putString(FOUND_TRACKS, Gson().toJson(foundTracks))        // Сохранение найденных треков
        outState.putString(UPDATE_TRACKS, Gson().toJson(updateTracks))      // Сохранение треков, бэкапнутых перед очисткой
        outState.putString(UPDATE_SEARCH, updateText)                       // Сохранение строки поиска, бэкапнутой перед очисткой
    }

    // Восстановление данных, необходимых для реализации поворота экрана
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        if (savedInstanceState == null)
            return

        var jsonString = savedInstanceState.getString(SEARCH_SCREEN_MODE, "")   // Восстановление состояния экрана
        if (jsonString != "") {
            screenState = Gson().fromJson<ScreenModes>(
                jsonString, object : TypeToken<ScreenModes>() {}.type)
        }

        jsonString = savedInstanceState.getString(FOUND_TRACKS, "")             // Восстановление списка треков
        if (jsonString != "") {
            foundTracks.clear()
            val tracks: ArrayList<Track> = Gson().fromJson<ArrayList<Track>>(
                jsonString, object : TypeToken<ArrayList<Track>>() {}.type)
            if (tracks != null)
                foundTracks.addAll(tracks)
        }

        jsonString = savedInstanceState.getString(UPDATE_TRACKS, "")           // Восстановление списка треков бэкапнутых перед очисткой
        if (jsonString != "" ) {
            updateTracks.clear()
            val tracks = Gson().fromJson<ArrayList<Track>>(
                jsonString, object : TypeToken<ArrayList<Track>>() {}.type)
            if (tracks != null)
                updateTracks.addAll(tracks)
        }
        updateText = savedInstanceState.getString(UPDATE_SEARCH, "")            // Восстановление строки поиска бэкапнутой перед очисткой
    }

    override fun onResume() {
        super.onResume()
        // Строковая переменная для поиска восстанавливается из inputEditText после того,
        //  как значения inputEditText были восстановлены дефолтными методами
        searchText = findViewById<EditText>(R.id.inputEditText).text.toString()
        // Установка состояния экрана после восстановления переменной состояния экрана или по-умолчанию, если первый вход
        setSearchScreenMode(screenState)
    }

    // Сохранение истории поиска в Shared Prefernces при унижтожении Activity для восстановления при новом создании Activity
    override fun onDestroy() {
        super.onDestroy()
        val sharedPrefs = getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY, Gson().toJson(historyTracks))
            .apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Реализация нажатия кнопки <Назад> экрана <Поиск> для перехода на главный экран
        val returnFromSearch = findViewById<Button>(R.id.return_from_search)
        returnFromSearch.setOnClickListener { finish() }

        // Считывание истории поиска из Shared Preferences
        val sharedPrefs = getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES, MODE_PRIVATE)
        val jsonString = sharedPrefs.getString(SEARCH_HISTORY, "")
        if (jsonString != "") {
            historyTracks.clear()
            val tracks = Gson().fromJson<ArrayList<Track>>(
                jsonString,
                object : TypeToken<ArrayList<Track>>() {}.type)
            if (tracks != null)
                historyTracks.addAll(tracks)
        }

        // Инициализация RecyclerView для просмотра результато поиска
        val recycler = findViewById<RecyclerView>(R.id.tracks_list)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TrackAdapter(foundTracks, historyTracks)

        // Инициализация RecyclerView для просмотра истории
        val recyclerHistory = findViewById<RecyclerView>(R.id.history_list)
        recyclerHistory.layoutManager = LinearLayoutManager(this)
        recyclerHistory.adapter = HistoryAdapter(historyTracks)

        // Редактируемый текст поискового запроса
        val searchEditText = findViewById<EditText>(R.id.inputEditText)

        // Реализация нажатия кнопки "Очистить поисковый запрос" (х)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            // Сохранить текст поискового запроса перед очисткой
            updateText = searchEditText.text.toString()
            // Сохранить список треков последнего поиска перед очисткоц
            updateTracks.clear()
            updateTracks.addAll((recycler.adapter as TrackAdapter).tracks)
            // Очистить поисковый запрос
            searchEditText.setText("")
            //Очистить список треков
            (recycler.adapter as TrackAdapter).tracks.clear()
            // Скрыть клавиатуру
            hideKeyboard()
            // Перерисовать список на экране
            (recycler.adapter as TrackAdapter).notifyDataSetChanged()
        }

        // Реализация редактирования поискового запроса
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.GONE      // Скрыть кнопку "Очистить" при пустой строке ввода
                    if (historyTracks.isNullOrEmpty())              // Если история поиска пустая
                        setSearchScreenMode(ScreenModes.SCR_READY)      // Переключиться на экран последнего поиска
                    else {                                          // Если история поиска не пустая
                        if (screenState != ScreenModes.SCR_HISTORY) {   // Отобразить историю поиска
                            (recyclerHistory.adapter as HistoryAdapter).notifyDataSetChanged()
                            setSearchScreenMode(ScreenModes.SCR_HISTORY)
                        }
                    }
                }
                else {                                      // Показать кнопку "Очистить" в ином случае
                    clearButton.visibility = View.VISIBLE
                    if (screenState == ScreenModes.SCR_HISTORY) {       // Если экран истории поиска
                        (recycler.adapter as TrackAdapter).notifyDataSetChanged()
                        setSearchScreenMode(ScreenModes.SCR_READY)      // Переключиться на экран последнего поиска
                    }
                }
                searchText = s.toString()                                       // Сохранить строку ввода в переменной
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        // Реализация переключения на экран истории при получении фокуса строки редактирования поискового запроса
        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            setSearchScreenMode(if (hasFocus && searchEditText.text.isNullOrBlank() && !historyTracks.isNullOrEmpty())
                ScreenModes.SCR_HISTORY else ScreenModes.SCR_READY)
        }

        // Реализация нажатия кнопки c лупой с левой стороны строки поиска
        // При не пустой строке поиска осуществляет новый запрос
        // При пустой строке поиска восстанавливает последнюю строку поиска и результаты последнего поиска
        val loupeButton = findViewById<ImageView>(R.id.loupeIcon)
        loupeButton.setOnClickListener {
            if (searchText.isNotBlank())
                getTracks(recycler.adapter as TrackAdapter)
            else {
                // Восстановить текст последего запроса
                searchEditText.setText(updateText)
                // Восстановить список треков последнего запроса
                (recycler.adapter as TrackAdapter).tracks.clear()
                (recycler.adapter as TrackAdapter).tracks.addAll(updateTracks)
                (recycler.adapter as TrackAdapter).notifyDataSetChanged()
                setSearchScreenMode(ScreenModes.SCR_READY)
            }
        }

        // Реализация нажатия кнопки "Обновить" после неудачного поиска
        val updateButton = findViewById<Button>(R.id.button_update)
        updateButton.setOnClickListener {
            getTracks(recycler.adapter as TrackAdapter)
        }

        // Реализация нажатия кнопки Done клавиатуры
        // Осуществляет новый запрос
        searchEditText.setOnEditorActionListener { _, actionId, _->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTracks(recycler.adapter as TrackAdapter)
            }
            false
        }

        // Реализация нажатия кнопки "Очистить историю"
        val clearHistoryButton = findViewById<Button>(R.id.button_clear_history)
        clearHistoryButton.setOnClickListener {
            (recyclerHistory.adapter as HistoryAdapter).history.clear()
            (recyclerHistory.adapter as HistoryAdapter).notifyDataSetChanged()
            setSearchScreenMode(ScreenModes.SCR_READY)
        }
    }

    // Скрыть клавиатуру
    private fun hideKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(findViewById<EditText>(R.id.inputEditText)
                .windowToken,0)
    }

    // Организация запроса
    private fun getTracks(adapter : TrackAdapter) {
        hideKeyboard()
        if (searchText.isNotBlank()) {
            setSearchScreenMode(ScreenModes.SCR_DOWNLOAD)
            iTuneService.searchTracks(searchText.toString()).enqueue(object : Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>,
                                        response: Response<TracksResponse>) {
                    if (response.code() == 200) {
                        adapter.tracks.clear()
                        if ((response.body()?.resultCount != null) && (response.body()?.results != null)) {
                            if (response.body()?.resultCount!! > 0) {
                                adapter.tracks.addAll(response.body()?.results!!)
                                setSearchScreenMode(ScreenModes.SCR_READY)      // данные готовы
                            } else setSearchScreenMode(ScreenModes.SCR_EMPTY)   // Сервер не вернул ни одного трека
                        } else setSearchScreenMode(ScreenModes.SCR_ERROR)       // Ошибка (API вернул null в данных)
                        // Перерисовать отображение всех треков
                        adapter.notifyDataSetChanged()
                    } else setSearchScreenMode(ScreenModes.SCR_ERROR)           // Ошибка (код ответа (response code) не равен 200)
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    setSearchScreenMode(ScreenModes.SCR_ERROR)                  // Ошибка (запрос окончился неудачей)
                }
            })
        }
    }

     private fun setSearchScreenMode(screenMode : ScreenModes) {
        var downloadView = View.GONE
        var emptyErrorView = View.GONE
        var linkErrorView = View.GONE
        var buttonView = View.GONE

        screenState = screenMode        // Сохранение состояние экрана в глобальной переменной
        when (screenMode) {
            ScreenModes.SCR_DOWNLOAD -> {
                findViewById<TextView>(R.id.text_on_error).setText(R.string.download_message)
                downloadView = View.VISIBLE
            }
            ScreenModes.SCR_EMPTY -> {
                findViewById<TextView>(R.id.text_on_error).setText(R.string.empty_error)
                emptyErrorView = View.VISIBLE
            }
            ScreenModes.SCR_ERROR -> {
                findViewById<TextView>(R.id.text_on_error).setText(R.string.net_error)
                linkErrorView = View.VISIBLE
                buttonView = View.VISIBLE
            }
            ScreenModes.SCR_READY -> {
                findViewById<LinearLayout>(R.id.layout_on_error).visibility = View.GONE         // Скрытие плейсхолдера
                findViewById<LinearLayout>(R.id.layout_on_history).visibility = View.GONE       // Скрытие истории поиска
                findViewById<RecyclerView>(R.id.tracks_list).visibility = View.VISIBLE          // Отображение списка треков
                return
            }
            ScreenModes.SCR_HISTORY -> {
                findViewById<RecyclerView>(R.id.tracks_list).visibility = View.GONE             // Скрытие списка треков
                findViewById<LinearLayout>(R.id.layout_on_error).visibility = View.GONE         // Скрытие плейсхолдера
                findViewById<LinearLayout>(R.id.layout_on_history).visibility = View.VISIBLE    // Отображение истории поиска
                return
            }
        }
        findViewById<LinearLayout>(R.id.layout_on_history).visibility = View.GONE       // Скрытие истории поиска
        findViewById<RecyclerView>(R.id.tracks_list).visibility = View.GONE             // Скрытие списка треков
        findViewById<ImageView>(R.id.image_download).visibility = downloadView          // Установка выбранной видимости картинки "Заграузка"
        findViewById<ImageView>(R.id.image_empty_error).visibility = emptyErrorView     // Установка выбранной видимости картинки "Пусто"
        findViewById<ImageView>(R.id.image_link_error).visibility = linkErrorView       // Установка выбранной видимости картинки "Нет связи"
        findViewById<Button>(R.id.button_update).visibility = buttonView                // Установка выбранной видимости кнопки "Обновить"
        findViewById<LinearLayout>(R.id.layout_on_error).visibility = View.VISIBLE      // Отображение настроенного плейсхолдера
     }
}
