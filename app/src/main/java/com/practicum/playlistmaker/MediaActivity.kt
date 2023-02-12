package com.practicum.playlistmaker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class MediaActivity : AppCompatActivity() {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        // Нажатие кнопки <Назад> экрана <Медиатека> для перехода на главный экран
        val return_from_settings = findViewById<Button>(R.id.return_from_media)
        return_from_settings.setOnClickListener { finish() }

        //Вывод укрупненной Mock-обложки одного из треков
        val image = findViewById<ImageView>(R.id.image_inet)
        Glide.with(this)
            .load("https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg")
            .placeholder(R.drawable.ic_sync_off)
            .centerInside()
            .transform(RoundedCorners(10))
            .into(image)

        // Вывод списка RecyclerView с Mock-данными, загруженными статичным методом класса Track
        val recycler = findViewById<RecyclerView>(R.id.tracksList)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TrackAdapter(Track.loadMockTrackList())
    }
}
