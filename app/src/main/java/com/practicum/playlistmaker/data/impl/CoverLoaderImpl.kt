package com.practicum.playlistmaker.data.impl

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.CoverLoader

class CoverLoaderImpl : CoverLoader {
    override fun load(cover : ImageView, coverUrl: String) {
        val artUrl: String? = coverUrl
        if (!artUrl.isNullOrBlank() && artUrl.length >= 14) {
            Glide.with(cover)
                .load(artUrl.substring(0, artUrl.length - 13) + "512x512bb.jpg")
                .placeholder(R.drawable.ic_placeholder)
                .centerInside()
                .transform(RoundedCorners(cover.resources.getDimensionPixelSize(R.dimen.placeholder_radius)))
                .into(cover)
        } else
            cover.setImageResource(R.drawable.ic_sync_off)
    }

    override fun loadSmall(cover : ImageView, coverUrl: String) {
        if (!coverUrl.isNullOrBlank()) {
            Glide.with(cover)
                .load(coverUrl)
                .placeholder(R.drawable.ic_no_reply)
                .centerInside()
                .transform(RoundedCorners(cover.resources.getDimensionPixelSize(R.dimen.track_cover_radius)))
                .into(cover)
        } else
            cover.setImageResource(R.drawable.ic_sync_off)
    }
}
