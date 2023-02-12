package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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

    // Сосотяния экрана поиска
    enum class ScreenModes {
        SCR_DOWNLOAD,       // Загрузка
        SCR_EMPTY,          // Ничего не нашлось
        SCR_ERROR,          // Проблемы со связью
        SCR_READY           // Данные готовы
    }

    // Переменные для сохранения результата последнего отображенного на экране запроса
    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val FOUND_TRACKS = "FOUND_TRACKS"
    }

    // Переменная для сохранения набранной строки поиска
    public var searchText : String = ""

    private val iTuneBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTuneBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTuneService = retrofit.create(iTunesApi::class.java)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_STRING, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_STRING, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Реализация нажатия кнопки <Назад> экрана <Поиск> для перехода на главный экран
        val return_from_search = findViewById<Button>(R.id.return_from_search)
        return_from_search.setOnClickListener { finish() }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        // Восстановление строки ввода поискового запроса
        inputEditText.setText(searchText)

        // Инициализация RecyclerView пустым списком
        val recycler = findViewById<RecyclerView>(R.id.tracks_list)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TrackAdapter(arrayListOf())

        // Реализация нажатия кнопки "Очистить поисковый запрос" (х)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            // Очистить поисковый запрос
            inputEditText.setText("")
            // Скрыть клавиатуру
            hideKeyboard()
            //Очистить список треков
            (recycler.adapter as TrackAdapter).tracks.clear()
            // Перерисовать список на экране
            (recycler.adapter as TrackAdapter).notifyDataSetChanged()
        }

        // Реализация редактирования поискового запроса
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty())  clearButton.visibility = View.GONE      // Скрыть кнопку "Очистить" при пустой строке ввода
                else                    clearButton.visibility = View.VISIBLE   // Показать кнопку "Очистить" в ином случае
                searchText = s.toString()                                       // Сохранить строку ввода в глобальной переменной
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(searchTextWatcher)

        // Реализация нажатия кнопки c лупой с левой стороны строки поиска (o-)
        val loupeButton = findViewById<ImageView>(R.id.loupeIcon)
        loupeButton.setOnClickListener {
            getTracks(recycler.adapter as TrackAdapter)
        }

        // Реализация нажатия кнопки "Обновить" после неудачного поиска
        val updateButton = findViewById<Button>(R.id.button_update)
        updateButton.setOnClickListener {
            getTracks(recycler.adapter as TrackAdapter)
        }

        // Реализация нажатия кнопки Done клавиатуры
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTracks(recycler.adapter as TrackAdapter)
            }
            false
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
        if (searchText.isNotBlank()) {
            hideKeyboard()
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
                        } else setSearchScreenMode(ScreenModes.SCR_ERROR)       // Ошибка (API вернула null в данных)
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
        var imageId : Int = R.drawable.ic_download_light
        var textId : Int = R.string.download_message
        var updateVisibility : Int = View.GONE
        val nightMode = AppCompatDelegate.getDefaultNightMode()

        when (screenMode) {
            ScreenModes.SCR_DOWNLOAD -> {
                when (nightMode) {
                    AppCompatDelegate.MODE_NIGHT_YES -> imageId = R.drawable.ic_download_dark
                    AppCompatDelegate.MODE_NIGHT_NO -> imageId = R.drawable.ic_download_light
                }
                textId = R.string.download_message
            }
            ScreenModes.SCR_EMPTY -> {
                when (nightMode) {
                    AppCompatDelegate.MODE_NIGHT_YES -> imageId = R.drawable.ic_empty_error_dark
                    AppCompatDelegate.MODE_NIGHT_NO -> imageId = R.drawable.ic_empty_error_light
                }
                textId = R.string.empty_error
            }
            ScreenModes.SCR_ERROR -> {
                when (nightMode) {
                    AppCompatDelegate.MODE_NIGHT_YES -> imageId = R.drawable.ic_link_error_dark
                    AppCompatDelegate.MODE_NIGHT_NO -> imageId = R.drawable.ic_link_error_light
                }
                textId = R.string.net_error
                updateVisibility = View.VISIBLE
            }
            ScreenModes.SCR_READY -> {
                findViewById<RecyclerView>(R.id.tracks_list).visibility = View.VISIBLE  // Отображение экрана списка треков
                findViewById<LinearLayout>(R.id.layout_on_error).visibility = View.GONE // Скрытие плейсхолдера
                return
            }
        }
        findViewById<ImageView>(R.id.image_on_error).setImageResource(imageId)      // Установка выбранной картинки
        findViewById<TextView>(R.id.text_on_error).setText(textId)                  // Установка выбранного сообщения
        findViewById<Button>(R.id.button_update).visibility = updateVisibility      // Установка выбранной видимости кнопки "Очистить"
        findViewById<RecyclerView>(R.id.tracks_list).visibility = View.GONE         // Скрытие с экрана списка треков
        findViewById<LinearLayout>(R.id.layout_on_error).visibility = View.VISIBLE  // Отображение настроенного плейсхолдера
     }
}
