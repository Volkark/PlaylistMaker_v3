package com.practicum.playlistmaker

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.Gson

class HistoryAdapter(
    internal val history: ArrayList<Track>
) : RecyclerView.Adapter<TrackViewHolder> () {

    val deBounce = DeBounce()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(history[position])
        holder.itemView.setOnClickListener {
            // Запуск выбранного трека из истории поиска в аудиоплеере не чаще 1 раза в 1000 мс
            if (deBounce.allowed())
                PlayerActivity.run(history[position], holder.itemView.context)
        }
    }
    override fun getItemCount() : Int = history.size
}
