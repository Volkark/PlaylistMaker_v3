package com.practicum.playlistmaker.ui.tracks

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.data.storage.ChoiceStore
import com.practicum.playlistmaker.domain.impl.DeBounceImpl
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.tracks.TrackAdapterView
import com.practicum.playlistmaker.ui.player.PlayerActivity

class HistoryAdapter(
    internal val history: ArrayList<Track>,
    private val view: TrackAdapterView,
) : RecyclerView.Adapter<TrackViewHolder> () {

    val deBounce = DeBounceImpl()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(history[position])
        holder.itemView.setOnClickListener {
            // Сохранение позиции выбранного трека и
            // Запуск выбранного трека в аудиоплеере не чаще 1 раза в 1000 мс
            if (deBounce.allowed()) {
                view.updateChoice(position)
                view.startPlayerActivity()
            }
        }
    }
    override fun getItemCount() : Int = history.size
}
