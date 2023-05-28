package com.practicum.playlistmaker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.CoverLoader

class MediaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        // Нажатие кнопки <Назад> экрана <Медиатека> для перехода на главный экран
        findViewById<Button>(R.id.return_from_media)
            .setOnClickListener { finish() }

        //Вывод укрупненной Mock-обложки одного из треков
        CoverLoader.loadSmall(
            findViewById<ImageView>(R.id.image_inet),
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        )
    }
}
