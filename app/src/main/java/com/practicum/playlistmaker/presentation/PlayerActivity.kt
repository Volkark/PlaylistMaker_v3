package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.MediaPlayer
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
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.TrackCover
import com.practicum.playlistmaker.domain.Track
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    enum class PlayerModes {
        PLR_NO_READY,       // Трек не готов к воспроизведению
        PLR_PLAYING,        // Трек проигрывается
        PLR_PAUSE,          // Трек остановлен
    }

    // View-элементы Activity, используемые в разных функциях класса (не только в onCreate)
    private val noReadyHoop by lazy { findViewById<ProgressBar>(R.id.progressBarPlayer) }
    private val playButton by lazy { findViewById<ImageView>(R.id.circle_button_play) }
    private val pauseButton by lazy { findViewById<ImageView>(R.id.circle_button_pause) }
    private val heartButton by lazy { findViewById<ImageView>(R.id.circle_button_heart) }
    private val selectedHeartButton by lazy { findViewById<ImageView>(R.id.circle_button_heart_selected) }

    private val playTime by lazy { findViewById<TextView>(R.id.playback_time) }

    private val mediaPlayer = MediaPlayer()
    private var playerState : PlayerModes = PlayerModes.PLR_NO_READY
    private var heartState : Boolean = false

    private val handler = Handler(Looper.getMainLooper())
    private val playTimeEvent = Runnable {
        val newTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        if (newTime != playTime.text)
            playTime.setText(newTime)
        setPlayTimeEvent()
    }

    private fun setPlayTimeEvent() {
        handler.postDelayed(playTimeEvent, 100)
    }

    private fun removePlayTimeEvent() {
        handler.removeCallbacks(playTimeEvent)
    }

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

        // lastTrack - глобальная переменная для передачи выбранного трека в Activity аудиоплеера
        if (lastTrack != null) {
            trackName.setText(lastTrack!!.trackName)
            artistName.setText(lastTrack!!.artistName)
            trackTime.setText(Track.trackTimeFormat(lastTrack!!.trackTimeMillis))
            collect.setText(lastTrack!!.collectionName)
            year.setText(lastTrack!!.releaseDate.substring(0,4))
            genre.setText(lastTrack!!.primaryGenreName)
            country.setText(lastTrack!!.country)
            TrackCover.set(artwork, lastTrack!!.artworkUrl100)      // Установка картинки трека

            val trackUrl : String? = lastTrack?.previewUrl
            if (!trackUrl.isNullOrBlank() && trackUrl.length >= 10) {
                preparePlayer(trackUrl)
            }
        }
        else { finish() }

        // переключение кнопок восроизведения и паузы
        playButton.setOnClickListener {
            startPlayer()
        }
        pauseButton.setOnClickListener {
            pausePlayer()
        }
        heartButton.setOnClickListener {
            selectedHeartButton.visibility = View.VISIBLE
            heartButton.visibility = View.INVISIBLE
            heartState = true
        }
        selectedHeartButton.setOnClickListener {
            selectedHeartButton.visibility = View.INVISIBLE
            heartButton.visibility = View.VISIBLE
            heartState = false
        }
    }

    override fun onPause() {
        super.onPause()
        if (!heartState)
            pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        removePlayTimeEvent()
        mediaPlayer.release()
    }

    private fun preparePlayer(trackUrl:String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            noReadyHoop.visibility = View.INVISIBLE
            pauseButton.visibility = View.INVISIBLE
            playButton.visibility = View.VISIBLE
            playerState = PlayerModes.PLR_PAUSE
        }
        mediaPlayer.setOnCompletionListener {
            pausePlayer()
            playTime.setText("0:00")
            // Если сердце выбрано, то запустить снова на проигрывание
            if (heartState)
                startPlayer()
        }
    }

    private fun startPlayer() {
        if (playerState == PlayerModes.PLR_PAUSE) {
            playButton.visibility = View.INVISIBLE
            pauseButton.visibility = View.VISIBLE
            mediaPlayer.start()
            playerState = PlayerModes.PLR_PLAYING
            setPlayTimeEvent()
        }
    }

    private fun pausePlayer() {
        if (playerState == PlayerModes.PLR_PLAYING) {
            pauseButton.visibility = View.INVISIBLE
            playButton.visibility = View.VISIBLE
            mediaPlayer.pause()
            playerState = PlayerModes.PLR_PAUSE
            removePlayTimeEvent()
        }
    }

    companion object {
        // Метод запуска экрана аудиоплеера без создания экземпляра класса с передачей в качестве параметра выбранного трека и контекста
        fun run(track : Track, context : Context) {
            lastTrack = track
            ContextCompat.startActivity(context, Intent(context, PlayerActivity::class.java),null)
        }
    }
}
