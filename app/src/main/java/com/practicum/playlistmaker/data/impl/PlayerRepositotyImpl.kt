package com.practicum.playlistmaker.data.impl

import android.content.Context
import com.practicum.playlistmaker.data.storage.ChoiceStore
import com.practicum.playlistmaker.data.storage.SEARCH_HISTORY
import com.practicum.playlistmaker.data.storage.TracksStore
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.models.Track

class PlayerRepositotyImpl() : PlayerRepository {
    override fun selectedTrack(context : Context) : Track? {
        var position = 0
        val choiceStore = ChoiceStore(context)
        if (choiceStore.wasSaved())
            position = choiceStore.getStored()!!
        val tracksStore = TracksStore(context, SEARCH_HISTORY)
        if (tracksStore.wasSaved())
            return tracksStore.getStored()!!.get(position)
        else
            return null
    }
}
