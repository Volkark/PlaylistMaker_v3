package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        // Нажатие кнопки <Назад> экрана <Медиатека> для перехода на главный экран
        val return_from_settings = findViewById<Button>(R.id.return_from_media)
        return_from_settings.setOnClickListener { finish() }
    }
}
