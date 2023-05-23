package com.practicum.playlistmaker.data.storage

// Работа с SharedPreferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val PLAY_LIST_MAKER_PREFERENCES = "play_list_maker_preferences"
const val SEARCH_HISTORY = "search_history"
const val APP_NIGHT_MODE = "app_night_mode"
const val SELECTED_TRACK = "selected_track"

abstract class SharedPreferences<T>(val context: Context, val cellName: String) {
    var value: T? = null

    open fun wasSaved(): Boolean {
        val jsonString =
            context.getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES, MODE_PRIVATE)
                .getString(cellName, "")
        if (jsonString != "") {
            value = Gson().fromJson<T>(
                jsonString,
                object : TypeToken<T>() {}.type
            )
        }
        return (value != null)
    }

    fun getStored() : T? {
        return value
    }

    fun save(stored_value : T) {
        context.getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES, MODE_PRIVATE)
            .edit()
            .putString(cellName, Gson().toJson(stored_value))
            .apply()
    }
}
