package com.practicum.playlistmaker.data

// Работа с SharedPreferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.models.Track

const val PLAY_LIST_MAKER_PREFERENCES = "play_list_maker_preferences"

abstract class UnsafeHardStorage<T>(final val context: Context, val cellName: String) {
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

class NightModeStore(context: Context, cellName: String) : UnsafeHardStorage<Int>(context, cellName) {
}

// Для класса ArrayList<Track> функция абстактного класса wasSaved(), подставляя вместо T выражение ArrayList<Track>
// преобразет сохранненную json-строку неправильно, при прямом подставлении ArrayList<Track> в переопределенной wasSaved()
// json-строка преобразуется правильно

class TracksStore(context: Context, cellName: String) : UnsafeHardStorage<ArrayList<Track>>(context, cellName) {
    override fun wasSaved(): Boolean {
        val jsonString =
            context.getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES, MODE_PRIVATE)
                .getString(cellName, "")
        if (jsonString != "") {
            value = Gson().fromJson<ArrayList<Track>>(
                jsonString,
                object : TypeToken<ArrayList<Track>>() {}.type
            )
        }
        return (value != null)
    }
}
