package com.practicum.playlistmaker.presentation

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.data.NightModeStore
import kotlin.Int as Int

const val APP_NIGHT_MODE = "app_night_mode"
const val SEARCH_HISTORY = "search_history"
const val LAST_TRACK = "last_track"
const val HISTORY_SIZE = 10

var lastTrack : Track? = null

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Работа с SharedPreferences через интерфейсы и абстрактные класса слоя data
        val nightModeStore = NightModeStore(this, APP_NIGHT_MODE)

        if (nightModeStore.wasSaved())                              // Если тема приложения сохранена в SharedPreferences
            AppCompatDelegate                                           // Восстановить тему приложения
                .setDefaultNightMode(nightModeStore.getStored()!!)
        else {                                                      // Иначе установить тему приложения по-умолчанию
            if (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Главный экран
        setContentView(R.layout.activity_main)

        // Выход из приложения
        findViewById<TextView>(R.id.return_from_app)
            .setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAndRemoveTask()
                else
                    finish()
            }

        // Реализация нажатия кнопки <Поиск>
        findViewById<Button>(R.id.button_search)
            .setOnClickListener {
                startActivity(
                    Intent(this, SearchActivity::class.java)
                )
            }

        // Реализация нажатия кнопки <Медиатека>
        findViewById<Button>(R.id.button_media)
            .setOnClickListener {
                startActivity(
                    Intent(this, MediaActivity::class.java)
                )
            }

        // Рнеализация нажатия кнопки <Настройки>
        findViewById<Button>(R.id.button_settings)
            .setOnClickListener {
                startActivity(
                    Intent(this, SettingsActivity::class.java)
                )
            }
   }
}