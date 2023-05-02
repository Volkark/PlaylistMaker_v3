package com.practicum.playlistmaker

//import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.Image
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    enum class PlayerModes {
        PLR_NO_READY,       // Трек не готов к воспроизведению
        PLR_PLAYING,        // Трек проигрывается
        PLR_PAUSE,          // Трек остановлен
    }

    private var mediaPlayer = MediaPlayer()
    private var playerState : PlayerModes = PlayerModes.PLR_NO_READY
    private var heartState : Boolean = false

    private val handler = Handler(Looper.getMainLooper())
    private val timeApSet = Runnable {
        val timeAp = findViewById<TextView>(R.id.time_ap)
        val newTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        if (newTime != timeAp.text)
            timeAp.setText(newTime)
        setTimeApDebounce()
    }

    private fun setTimeApDebounce() {
        handler.postDelayed(timeApSet, 100)
    }

    private fun removeTimeApDebounce() {
        handler.removeCallbacks(timeApSet)
    }

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

            val trackUrl : String = lastTrack!!.previewUrl
            if (!trackUrl.isNullOrBlank() && trackUrl.length >= 10) {
                preparePlayer(trackUrl)
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

        // переключение кнопок восроизведения и паузы
        findViewById<ImageView>(R.id.circle_button_play).setOnClickListener {
            startPlayer()
        }
        findViewById<ImageView>(R.id.circle_button_pause).setOnClickListener {
            pausePlayer()
        }
        findViewById<ImageView>(R.id.circle_button_heart).setOnClickListener {
            findViewById<ImageView>(R.id.circle_button_heart_selected).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.circle_button_heart).visibility = View.INVISIBLE
            heartState = true
        }
        findViewById<ImageView>(R.id.circle_button_heart_selected).setOnClickListener {
            findViewById<ImageView>(R.id.circle_button_heart_selected).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.circle_button_heart).visibility = View.VISIBLE
            heartState = false
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeTimeApDebounce()
        mediaPlayer.release()
    }

    private fun preparePlayer(trackUrl:String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            findViewById<ImageView>(R.id.circle_button_play).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.progressBarPlayer).visibility = View.INVISIBLE
            playerState = PlayerModes.PLR_PAUSE
        }
        mediaPlayer.setOnCompletionListener {
            pausePlayer()
            findViewById<TextView>(R.id.time_ap).setText("0:00")
            // Если сердце выбрано, то запустить снова на проигрывание
            if (heartState)
                startPlayer()
        }
    }

    private fun startPlayer() {
        if (playerState == PlayerModes.PLR_PAUSE) {
            findViewById<ImageView>(R.id.circle_button_play).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.circle_button_pause).visibility = View.VISIBLE
            mediaPlayer.start()
            playerState = PlayerModes.PLR_PLAYING
            setTimeApDebounce()
        }
    }

    private fun pausePlayer() {
        if (playerState == PlayerModes.PLR_PLAYING) {
            findViewById<ImageView>(R.id.circle_button_play).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.circle_button_pause).visibility = View.INVISIBLE
            mediaPlayer.pause()
            playerState = PlayerModes.PLR_PAUSE
            removeTimeApDebounce()
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
