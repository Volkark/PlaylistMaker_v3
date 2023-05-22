package com.practicum.playlistmaker.domain.impl

import android.os.Handler
import android.os.Looper

// Класс для предотвращения "дребезга" при кликах на объекты
class DeBounceImpl() {
    companion object {
        private const val DEBOUNCE_DELAY = 1000L
    }

    private var isSelectAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    fun allowed() : Boolean {
        val current = isSelectAllowed
        if (isSelectAllowed) {
            isSelectAllowed = false
            handler.postDelayed({ isSelectAllowed = true }, DEBOUNCE_DELAY)
        }
        return current
    }
}
