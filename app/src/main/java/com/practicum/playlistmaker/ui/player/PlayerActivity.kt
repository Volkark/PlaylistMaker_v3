package com.practicum.playlistmaker.ui.player

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.CoverLoader
import com.practicum.playlistmaker.data.impl.PlayerRepositotyImpl
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.player.PlayerView

class PlayerActivity : AppCompatActivity(), PlayerView {
    private val playerPresenter = Creator.providePlayerPresenter(this, this)

    // View-элементы Activity, используемые в разных функциях класса (не только в onCreate)
    private val noReadyHoop by lazy { findViewById<ProgressBar>(R.id.progressBarPlayer) }
    private val playButton by lazy { findViewById<ImageView>(R.id.circle_button_play) }
    private val pauseButton by lazy { findViewById<ImageView>(R.id.circle_button_pause) }
    private val heartButton by lazy { findViewById<ImageView>(R.id.circle_button_heart) }
    private val selectedHeartButton by lazy { findViewById<ImageView>(R.id.circle_button_heart_selected) }
    private val trackName by lazy { findViewById<TextView>(R.id.track_name_ap) }
    private val artistName by lazy { findViewById<TextView>(R.id.artist_ap) }
    private val trackTime by lazy { findViewById<TextView>(R.id.cont1) }
    private val collect by lazy { findViewById<TextView>(R.id.cont2) }
    private val year by lazy { findViewById<TextView>(R.id.cont3) }
    private val genre by lazy { findViewById<TextView>(R.id.cont4) }
    private val country by lazy { findViewById<TextView>(R.id.cont5) }
    private val artwork by lazy {  findViewById<ImageView>(R.id.artworkUrl512х512) }

    private val playTime by lazy { findViewById<TextView>(R.id.playback_time) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Использование разных макетов для портретной и ландшафтной ориентаций дисплея
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            setContentView(R.layout.activity_player_land)
        else
            setContentView(R.layout.activity_player)

        // Нажатие кнопки <Назад> экрана <Аудиоплееер> для перехода на главный экран
        findViewById<Button>(R.id.return_from_player)
            .setOnClickListener { finish() }

        // Инициализация плеера
        playerPresenter.create()

        // переключение кнопок восроизведения и паузы
        playButton.setOnClickListener {
            playerPresenter.start()
        }
        pauseButton.setOnClickListener {
            playerPresenter.stop()
        }

        // переключение кнопки "сердца"
        heartButton.setOnClickListener {
            playerPresenter.heart(true)
        }
        selectedHeartButton.setOnClickListener {
            playerPresenter.heart(false)
        }
    }

    override fun trackState(track : Track?) {
        if (track != null) {
            trackName.setText(track.trackName)
            artistName.setText(track.artistName)
            trackTime.setText(track.trackTime)
            collect.setText(track.collectionName)
            year.setText(track.releaseYear)
            genre.setText(track.primaryGenreName)
            country.setText(track.country)

            val coverUrl : String = track.artworkUrl100
            if (coverUrl.isNotBlank() && coverUrl.length >= 10) {
                CoverLoader
                    .load(artwork, coverUrl)
                }
            }
        else
            finish()
    }

    override fun heartState(state : Boolean) {
        if (state) {
            selectedHeartButton.visibility = View.VISIBLE
            heartButton.visibility = View.INVISIBLE
        } else {
            selectedHeartButton.visibility = View.INVISIBLE
            heartButton.visibility = View.VISIBLE
        }
    }

    override fun playingState() {
        playButton.visibility = View.INVISIBLE
        pauseButton.visibility = View.VISIBLE
    }

    override fun pauseState() {
        pauseButton.visibility = View.INVISIBLE
        playButton.visibility = View.VISIBLE
    }

    override fun readyState() {
        noReadyHoop.visibility = View.INVISIBLE
        pauseState()
    }

    override fun timing(newTime : String) {
        if (newTime != playTime.text)
            playTime.setText(newTime)
    }

    override fun onPause() {
        super.onPause()
        playerPresenter.lostFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerPresenter.destroy()
    }
}
