package com.practicum.playlistmaker.util

import android.content.Context
import com.practicum.playlistmaker.data.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.presentation.player.PlayerPresenter
import com.practicum.playlistmaker.presentation.player.PlayerView
import com.practicum.playlistmaker.presentation.tracks.TracksSearchPresenter
import com.practicum.playlistmaker.presentation.tracks.TracksView

object Creator {
    private fun getTracksRepository(context : Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context : Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun provideTracksSearchPresenter(tracksView: TracksView, context: Context): TracksSearchPresenter {
        return TracksSearchPresenter(tracksView, context)
    }

    fun providePlayerPresenter(playerView: PlayerView, context: Context): PlayerPresenter {
        return PlayerPresenter(playerView, context)
    }

    fun providePlayerInteractor() : PlayerInteractor {
        return PlayerInteractorImpl()
    }
}
