package com.practicum.playlistmaker.data.impl

import android.content.Context
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.TracksRequest
import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.data.storage.ChoiceStore
import com.practicum.playlistmaker.data.storage.SEARCH_HISTORY
import com.practicum.playlistmaker.data.storage.TracksStore
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Resource<ArrayList<Track>> {
        val response = networkClient.doRequest(TracksRequest(expression))
        return when (response.resultCode) {
            -1 -> Resource.Error("Проверьте подключение к интернету")
            200 -> {
                Resource.Success((response as TracksResponse).results.map {
                    Track(
                        trackId = it.trackId ?: 0,                      // Уникальный номер трека
                        trackName = it.trackName ?: "",                 // Название композиции
                        artistName = it.artistName ?: "",               // Имя исполнителя
                        collectionName = it.collectionName ?: "",       // Название альбома
                        releaseDate = it.releaseDate ?: "",             // Дата релиза трека
                        primaryGenreName = it.primaryGenreName ?: "",   // Жанр трека
                        country = it.country ?: "",                     // Страна исполнителя
                        trackTimeMillis = it.trackTimeMillis ?: 0,      // Продолжительность трека в миллисекундах
                        artworkUrl100 = it.artworkUrl100 ?: "",         // Ссылка на изображение обложки
                        previewUrl = it.previewUrl ?: ""                // Ссылка на отрывок трека
                    ) } as ArrayList<Track>)
            }
            else -> Resource.Error("Ошибка сервера")
        }
    }

    // Сохранение истории поиска в Shared Prefernces
    override fun storeHistory(context : Context, history : ArrayList<Track>) {
        TracksStore(context, SEARCH_HISTORY)
            .save(history)
    }

    // Сохранение позиции выбранного трека в Shared Preferеnces
    override fun storePosition(context : Context, position : Int) {
        ChoiceStore(context)
            .save(position)
    }

    // Считывание истории поиска из Shared Preferеnces
    override fun getHistory(context: Context): ArrayList<Track> {
        val tracksStore = TracksStore(context, SEARCH_HISTORY)
        if (tracksStore.wasSaved())
            return tracksStore.getStored()!!
        else
            return arrayListOf()
    }
}
