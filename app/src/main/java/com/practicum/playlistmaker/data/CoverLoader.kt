package com.practicum.playlistmaker.data

import android.widget.ImageView

// Интерфейс загрузчика обложки
interface CoverLoader {
    fun load(cover : ImageView, coverUrl: String)
    fun loadSmall(cover : ImageView, coverUrl: String)
}
