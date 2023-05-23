package com.practicum.playlistmaker.ui.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.CoverLoader
import com.practicum.playlistmaker.domain.models.Track

class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.track_view, parentView, false)
)   {
        private val trackName: TextView       // Композиция
        private val artistName: TextView      // Исполнитель
        private val trackTime: TextView       // Продолжительность
        private val artwork: ImageView        // Изображение обложки
        init {
            trackName = itemView.findViewById<TextView >(R.id.trackName)
            artistName = itemView.findViewById<TextView>(R.id.artistName)
            trackTime = itemView.findViewById<TextView>(R.id.trackTime)
            artwork = itemView.findViewById<ImageView>(R.id.artworkUrl100)
        }
    fun bind(track: Track) {
        trackName.setText(track.trackName)
        artistName.setText(track.artistName)
        trackTime.setText(track.trackTime)
        CoverLoader
            .loadSmall(artwork, track.artworkUrl100)
    }
}
