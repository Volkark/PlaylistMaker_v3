package com.practicum.playlistmaker.ui.tracks

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.data.storage.ChoiceStore
import com.practicum.playlistmaker.data.storage.TracksStore
import com.practicum.playlistmaker.domain.impl.DeBounceImpl
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.PlayerActivity
import com.practicum.playlistmaker.data.storage.SEARCH_HISTORY
import com.practicum.playlistmaker.presentation.tracks.TrackAdapterView
import com.practicum.playlistmaker.presentation.tracks.TracksView
import kotlin.collections.ArrayList

class TrackAdapter(
    internal var tracks: ArrayList<Track>,
    private val view: TrackAdapterView,
) : RecyclerView.Adapter<TrackViewHolder> () {

    val deBounce = DeBounceImpl()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            // Добавление выбранного трека в историю поиска и
            // запуск выбранного трека в аудиоплеере не чаще 1 раза в 1000 мс
            if (deBounce.allowed()) {
                view.updateHistory(tracks[position])
                view.startPlayerActivity()
            }
        }
    }

    override fun getItemCount() : Int = tracks.size
}

