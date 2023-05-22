package com.practicum.playlistmaker.ui.tracks

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.data.TracksStore
import com.practicum.playlistmaker.domain.DeBounce
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.HISTORY_SIZE
import com.practicum.playlistmaker.ui.player.PlayerActivity
import com.practicum.playlistmaker.ui.SEARCH_HISTORY
import kotlin.collections.ArrayList

class TrackAdapter(
    internal var tracks: ArrayList<Track>,
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
            // Сохранение истории поиска в Shared Prefernces
            TracksStore(holder.itemView.context, SEARCH_HISTORY)
                .save(history)

            // Запуск выбранного трека в аудиоплеере не чаще 1 раза в 1000 мс
            if (deBounce.allowed())
                PlayerActivity.run(track, holder.itemView.context)
        }
    }

    override fun getItemCount() : Int = tracks.size
}
