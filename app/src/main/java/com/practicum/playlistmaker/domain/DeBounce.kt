package com.practicum.playlistmaker.domain

import android.os.Handler
import android.os.Looper

// Класс для предотвращения "дребезга" при кликах на объекты
class DeBounce() {
    private var isSelectAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    fun allowed() : Boolean {
        val current = isSelectAllowed
        if (isSelectAllowed) {
            isSelectAllowed = false
            handler.postDelayed({ isSelectAllowed = true }, 1000L)
        }
        return current
    }
}
