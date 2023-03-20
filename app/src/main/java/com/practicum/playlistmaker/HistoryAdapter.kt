package com.practicum.playlistmaker

import android.content.Intent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.Gson

class HistoryAdapter(
    internal val history: ArrayList<Track>
) : RecyclerView.Adapter<TrackViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(history[position])
        holder.itemView.setOnClickListener {
            // Запуск выбранного трека из истории поиска в аудиоплеере
            PlayerActivity.run(history[position], holder.itemView.context)
        }
    }
    override fun getItemCount() : Int = history.size
}
