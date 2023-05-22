package com.practicum.playlistmaker.ui.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.TrackCover
import com.practicum.playlistmaker.domain.models.Track

class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.track_view, parentView, false)
)   {
        private val trackName: TextView       // Композиция
        private val artistName: TextView      // Исполнитель и Продолжительность
        private val trackTime: TextView       // Исполнитель и Продолжительность
        private val artwork: ImageView        // Изображение обложки
        init {
            trackName = itemView.findViewById(R.id.trackName)
            artistName = itemView.findViewById(R.id.artistName)
            trackTime = itemView.findViewById(R.id.trackTime)
            artwork = itemView.findViewById(R.id.artworkUrl100)
        }
    fun bind(track: Track) {
        trackName.setText(track.trackName)
        artistName.setText(track.artistName)
        trackTime.setText(Track.trackTimeFormat(track.trackTimeMillis))
        TrackCover.setSmall(artwork, track.artworkUrl100)
    }
}
