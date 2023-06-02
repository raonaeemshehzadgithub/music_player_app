package com.app.musicplayer.models

data class ListData<DataType>(
    var items: List<DataType> = arrayListOf()
) {
    companion object {
        fun fromTracks(
            tracks: List<Track>
        ): ListData<Track> {
            return ListData(tracks)
        }

        fun fromAlbums(
            albums: List<Album>
        ): ListData<Album> {
            return ListData(albums)
        }
    }
}