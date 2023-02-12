package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*

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
    fun bind(track: Track) {
        trackNameView.text = track.trackName
        // SimpleDateFormat не использован, т.к. добавлен в API level 24, а в build.gradle(:app) задано: minSdk 21
        // artistAndTimeView.text = model.artistName + " - " + SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime)
        artistAndTimeView.text = String.format("%s - %d:%02d",
            track.artistName,
            (track.trackTimeMillis / 60000).toInt(),
            ((track.trackTimeMillis % 60000) / 1000).toInt())
        Glide.with(artworkUrl100View.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_no_reply)
            .centerInside()
            .transform(RoundedCorners(artworkUrl100View.resources.getDimensionPixelSize(R.dimen.track_cover_radius)))
            .into(artworkUrl100View)
    }
}
