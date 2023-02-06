package com.practicum.playlistmaker

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
    }

    // Переменная для сохранения набранной строки поиска
    public var searchText : String = ""

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
        // Установка фокуса на строку ввода поискового запроса
        inputEditText.requestFocus()
        // Восстановление строки ввода поискового запроса
        inputEditText.setText(searchText)

        // Реализация нажатия кнопки "Очистить поисковый запрос" (х)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            // Очистить поисковый запрос
            inputEditText.setText("")
            // Скрыть клавиатуру
            (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.
                hideSoftInputFromWindow(inputEditText.windowToken,0)
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
    }
}
