package com.practicum.playlistmaker.domain.impl

import android.content.Context
import com.practicum.playlistmaker.data.storage.SEARCH_HISTORY
import com.practicum.playlistmaker.data.storage.TracksStore
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.tracks.SearchActivity
import com.practicum.playlistmaker.util.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
    }

    override fun clearTracksHistory(context : Context, history: ArrayList<Track>) {
        history.clear()
        repository.storeHistory(context, history)
    }

    override fun getTracksHistory(context : Context) : ArrayList<Track> {
        return repository.getHistory(context)
    }

    override fun updateTracksHistory(context : Context, history: ArrayList<Track>, track: Track) {
        if (history.contains(track)) history.remove(track)
        else if (history.size == SearchActivity.HISTORY_SIZE) history.removeAt(SearchActivity.HISTORY_SIZE - 1)
        history.add(0, track)
        repository.storeHistory(context, history)
        repository.storePosition(context, 0)
    }

    override fun updatePosition(context : Context, position : Int) {
        repository.storePosition(context, position)
    }
}
