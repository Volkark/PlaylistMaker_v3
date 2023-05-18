package com.practicum.playlistmaker.domain.models

data class Track (
    val trackId: Long,              // Уникальный номер трека
    val trackName: String,          // Название композиции
    val artistName: String,         // Имя исполнителя
    val collectionName: String,     // Название альбома
    val releaseDate: String,        // Год релиза трека
    val primaryGenreName: String,   // Жанр трека
    val country: String,            // Страна исполнителя
    val trackTimeMillis: Long,      // Продолжительность трека в миллисекндах
    val artworkUrl100: String,      // Ссылка на изображение обложки
    val previewUrl:String,          // Ссылка на отрывок трека
) {
    // Static метод для перевода времени в миллисекундак в текстовый вид
    companion object {
        fun trackTimeFormat(duration: Long): String {
            return String.format(
                "%d:%02d",
                (duration / 60000).toInt(),
                ((duration % 60000) / 1000).toInt()
            )
        }
    }
}
