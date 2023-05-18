package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TracksRequest
import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): ArrayList<Track> {
        val response = networkClient.doRequest(TracksRequest(expression))
        if (response.resultCode == 200) {
            return (response as TracksResponse).results.map {
                Track(
                    it.trackId,             // Уникальный номер трека
                    it.trackName,           // Название композиции
                    it.artistName,          // Имя исполнителя
                    it.collectionName,      // Название альбома
                    it.releaseDate,         // Год релиза трека
                    it.primaryGenreName,    // Жанр трека
                    it.country,             // Страна исполнителя
                    it.trackTimeMillis,     // Продолжительность трека в миллисекндах
                    it.artworkUrl100,       // Ссылка на изображение обложки
                    it.previewUrl           // Ссылка на отрывок трека
                ) } as ArrayList<Track>
        } else {
            return arrayListOf()
        }
    }
}
