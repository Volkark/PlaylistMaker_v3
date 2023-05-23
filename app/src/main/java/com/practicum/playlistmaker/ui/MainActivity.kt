package com.practicum.playlistmaker.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.storage.NightModeStore
import com.practicum.playlistmaker.ui.tracks.SearchActivity

var searchText : String = ""                            // Набранная строка поиска
var lastSearchText : String = ""                        // Последняя строка, с которой был запущен поиск

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Работа с SharedPreferences через интерфейсы и абстрактные класса слоя data
        val nightModeStore = NightModeStore(this)

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