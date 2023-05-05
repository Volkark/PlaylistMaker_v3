package com.practicum.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val PLAY_LIST_MAKER_PREFERENCES = "play_list_maker_preferences"
const val APP_NIGHT_MODE = "app_night_mode"
const val SEARCH_HISTORY = "search_history"
const val LAST_TRACK = "last_track"
const val HISTORY_SIZE = 10

var lastTrack : Track? = null

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создание экземпляра Shared Preferences
        val sharedPrefs = getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES, MODE_PRIVATE)
        // Сохраненная тема приложения из Shared Preferences
        val jsonString = sharedPrefs.getString(APP_NIGHT_MODE, "")
        // Тема приложения по-умолчанию
        var appNightMode = AppCompatDelegate.MODE_NIGHT_NO
        // Если тема приложения не сохранена
        if (jsonString == "") {
            // Установка темной (Dark) темы приложения при системной "Темной теме / Ночном режиме"
            if (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                appNightMode = AppCompatDelegate.MODE_NIGHT_YES
            AppCompatDelegate.setDefaultNightMode(appNightMode)
            }
        // Если тема приложения сохранена - восстановить тему приложения
        else {
            AppCompatDelegate.setDefaultNightMode(Gson().fromJson(jsonString, Int :: class.java))
        }

        // Главный экран
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.return_from_app).setOnClickListener { finish() }

        // Реализация нажатия кнопки <Поиск>
        val button_search = findViewById<Button>(R.id.button_search)
        button_search.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

        // Реализация нажатия кнопки <Медиатека>
        val button_media = findViewById<Button>(R.id.button_media)
        button_media.setOnClickListener {
            val displayIntent = Intent(this, MediaActivity::class.java)
            startActivity(displayIntent)
        }

        // Рнеализация нажатия кнопки <Настройки>
        val button_settings = findViewById<Button>(R.id.button_settings)
        button_settings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
   }
}