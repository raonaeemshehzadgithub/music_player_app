package com.app.musicplayer.models

data class ListData<DataType>(
    var items: List<DataType> = arrayListOf()
) {
    companion object {
        fun fromSongs(
            songs: List<Track>
        ): ListData<Track> {
            return ListData(songs)
        }
    }
}