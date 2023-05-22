package com.practicum.playlistmaker.ui.tracks

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.domain.DeBounce
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.PlayerActivity
import com.practicum.playlistmaker.ui.tracks.TrackViewHolder

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
