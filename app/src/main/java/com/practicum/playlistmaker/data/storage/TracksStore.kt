package com.practicum.playlistmaker.data.storage

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.models.Track

class TracksStore(context: Context, cellName: String) : SharedPreferences<ArrayList<Track>>(context, cellName) {
    override fun wasSaved(): Boolean {
        val jsonString =
            context.getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
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
