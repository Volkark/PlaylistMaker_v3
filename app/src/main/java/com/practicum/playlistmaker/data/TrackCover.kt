package com.practicum.playlistmaker.data

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R

interface TrackCover {
    companion object {
        fun set(trackImage : ImageView, url100: String?) {
            val artUrl: String? = url100
            if (!artUrl.isNullOrBlank() && artUrl.length >= 14) {
                Glide.with(trackImage)
                    .load(artUrl.substring(0, artUrl.length - 13) + "512x512bb.jpg")
                    .placeholder(R.drawable.ic_placeholder)
                    .centerInside()
                    .transform(RoundedCorners(trackImage.resources.getDimensionPixelSize(R.dimen.placeholder_radius)))
                    .into(trackImage)
            }
            else
                trackImage.setImageResource(R.drawable.ic_sync_off)
        }
        fun setSmall(trackImage : ImageView, url100: String?) {
            if (!url100.isNullOrBlank()) {
                Glide.with(trackImage)
                    .load(url100)
                    .placeholder(R.drawable.ic_no_reply)
                    .centerInside()
                    .transform(RoundedCorners(trackImage.resources.getDimensionPixelSize(R.dimen.track_cover_radius)))
                    .into(trackImage)
            }
            else
                trackImage.setImageResource(R.drawable.ic_sync_off)
        }
    }
}
