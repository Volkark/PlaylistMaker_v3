package com.practicum.playlistmaker

import android.app.Activity
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.presentation.TracksSearchController
import com.practicum.playlistmaker.ui.tracks.TrackAdapter

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideTracksSearchController(activity: Activity, adapter: TrackAdapter): TracksSearchController {
        return TracksSearchController(activity, adapter)
    }
}
