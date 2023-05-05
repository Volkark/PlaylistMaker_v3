package com.practicum.playlistmaker

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrackAdapter(
    internal val tracks: ArrayList<Track>,
    internal val history: ArrayList<Track>
) : RecyclerView.Adapter<TrackViewHolder> () {

    val deBounce = DeBounce()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            // Добавление выбранного трека в историю поиска даже при "быстром" выборе
            val track = tracks[position]
            if (history.contains(track)) history.remove(track)
            else if (history.size == HISTORY_SIZE) history.removeAt(HISTORY_SIZE - 1)
            history.add(0, track)

            // Запуск выбранного трека в аудиоплеере не чаще 1 раза в 1000 мс
            if (deBounce.allowed())
                PlayerActivity.run(track, holder.itemView.context)
        }
    }

    override fun getItemCount() : Int = tracks.size
}
