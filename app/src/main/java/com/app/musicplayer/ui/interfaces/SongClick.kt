package com.app.musicplayer.ui.interfaces

import com.app.musicplayer.models.Song

interface SongClick {
    fun onSongClick(position: Int, songPath:String, arrayList:ArrayList<Song>)
}