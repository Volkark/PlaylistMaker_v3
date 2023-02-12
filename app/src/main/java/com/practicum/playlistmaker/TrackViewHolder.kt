package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.track_view, parentView, false)
)   {
        private val trackNameView: TextView          // Композиция
        private val artistAndTimeView: TextView      // Исполнитель и Продолжительность
        private val artworkUrl100View: ImageView     // Изображение обложки
        init {
            trackNameView = itemView.findViewById(R.id.trackName)
            artistAndTimeView = itemView.findViewById(R.id.artistAndTime)
            artworkUrl100View = itemView.findViewById(R.id.artworkUrl100)
        }
    fun bind(model: Track) {
        trackNameView.text = model.trackName
        artistAndTimeView.text = model.artistName + " - " + model.trackTime
        Glide.with(artworkUrl100View.context)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_sync_off)
            .centerInside()
            .transform(RoundedCorners(10))
            .into(artworkUrl100View)
    }
}
