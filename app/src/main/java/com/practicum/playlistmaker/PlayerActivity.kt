package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Нажатие кнопки <Назад> экрана <Аудиоплееер> для перехода на главный экран
        val returnFromPlayer = findViewById<Button>(R.id.return_from_player)
        returnFromPlayer.setOnClickListener { finish() }

        // View-элементы Activity
        val trackName = findViewById<TextView>(R.id.track_name_ap)
        val artistName = findViewById<TextView>(R.id.artist_ap)
        val trackTime = findViewById<TextView>(R.id.cont1)
        val collect = findViewById<TextView>(R.id.cont2)
        val year = findViewById<TextView>(R.id.cont3)
        val genre = findViewById<TextView>(R.id.cont4)
        val country = findViewById<TextView>(R.id.cont5)
        val artworkUrl =  findViewById<ImageView>(R.id.artworkUrl512х512)

        // lastTrack - глобальная переменная для передачи выбранного трека в Activity аудиоплеера
        if (lastTrack != null) {
            trackName.setText(lastTrack!!.trackName)
            artistName.setText(lastTrack!!.artistName)
            trackTime.setText(
                String.format("%d:%02d",
                    (lastTrack!!.trackTimeMillis / 60000).toInt(),
                    ((lastTrack!!.trackTimeMillis % 60000) / 1000).toInt()))
            collect.setText(lastTrack!!.collectionName)
            year.setText(lastTrack!!.releaseDate.substring(0,4))
            genre.setText(lastTrack!!.primaryGenreName)
            country.setText(lastTrack!!.country)

            var art : String = lastTrack!!.artworkUrl100
            if (!art.isNullOrBlank() && art.length >= 14) {
                Glide.with(this)
                    .load(art.substring(0, art.length - 13) + "512x512bb.jpg")
                    .placeholder(R.drawable.ic_placeholder)
                    .centerInside()
                    .transform(RoundedCorners(artworkUrl.resources.getDimensionPixelSize(R.dimen.placeholder_radius)))
                    .into(artworkUrl)
            }
            else { artworkUrl.setImageResource(R.drawable.ic_sync_off)
            }
        }
        else {
            trackName.setText("")
            artistName.setText("")
            trackTime.setText("0:00")
            collect.setText("")
            year.setText("")
            genre.setText("")
            country.setText("")
            artworkUrl.setImageResource(R.drawable.ic_sync_off)
        }
    }

    // Метод запуска аудиоплеера без создания экземпляра класса с передачей в качестве параметра выбранного трека и контекста
    companion object {
        fun run(track : Track, context : Context) {
            lastTrack = track
            ContextCompat.startActivity(context, Intent(context, PlayerActivity::class.java),null)
        }
    }
}
