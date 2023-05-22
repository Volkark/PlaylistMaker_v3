package com.practicum.playlistmaker.ui.player

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.impl.CoverLoaderImpl
import com.practicum.playlistmaker.data.impl.PlayerRepositotyImpl
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.player.PlayerView
import com.practicum.playlistmaker.ui.lastTrack

class PlayerActivity : AppCompatActivity(), PlayerView {
    private val playerPresenter = Creator.providePlayerPresenter(this, this)

    // View-элементы Activity, используемые в разных функциях класса (не только в onCreate)
    private val noReadyHoop by lazy { findViewById<ProgressBar>(R.id.progressBarPlayer) }
    private val playButton by lazy { findViewById<ImageView>(R.id.circle_button_play) }
    private val pauseButton by lazy { findViewById<ImageView>(R.id.circle_button_pause) }
    private val heartButton by lazy { findViewById<ImageView>(R.id.circle_button_heart) }
    private val selectedHeartButton by lazy { findViewById<ImageView>(R.id.circle_button_heart_selected) }

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

        // View-элементы Activity, используемые только в onCreate
        val trackName = findViewById<TextView>(R.id.track_name_ap)
        val artistName = findViewById<TextView>(R.id.artist_ap)
        val trackTime = findViewById<TextView>(R.id.cont1)
        val collect = findViewById<TextView>(R.id.cont2)
        val year = findViewById<TextView>(R.id.cont3)
        val genre = findViewById<TextView>(R.id.cont4)
        val country = findViewById<TextView>(R.id.cont5)
        val artwork =  findViewById<ImageView>(R.id.artworkUrl512х512)

        val lastTrack = PlayerRepositotyImpl().selectedTrack(this)

        if (lastTrack != null) {
            trackName.setText(lastTrack.trackName)
            artistName.setText(lastTrack.artistName)
            trackTime.setText(lastTrack.trackTime)
            collect.setText(lastTrack.collectionName)
            year.setText(lastTrack.releaseYear)
            genre.setText(lastTrack.primaryGenreName)
            country.setText(lastTrack.country)

            val coverUrl : String = lastTrack.artworkUrl100
            if (!coverUrl.isNullOrBlank() && coverUrl.length >= 10) {
                CoverLoaderImpl()
                    .load(artwork, coverUrl)
            }

            val trackUrl : String = lastTrack.previewUrl
            if (!trackUrl.isNullOrBlank() && trackUrl.length >= 10) {
                preparePlayer(trackUrl)
            }
        }
        else {
            finish()
            return
        }

        // переключение кнопок восроизведения и паузы
        playButton.setOnClickListener {
            playerPresenter.start()
        }
        pauseButton.setOnClickListener {
            playerPresenter.stop()
        }
        heartButton.setOnClickListener {
            playerPresenter.heart(true)
        }
        selectedHeartButton.setOnClickListener {
            playerPresenter.heart(false)
        }
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

    private fun preparePlayer(trackUrl:String) {
        playerPresenter.create(trackUrl)
    }
}
