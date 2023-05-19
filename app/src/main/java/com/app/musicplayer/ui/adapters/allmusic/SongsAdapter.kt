package com.app.musicplayer.ui.adapters.allmusic

import android.content.Context
import com.app.musicplayer.interator.songs.SongsInteractor
import com.app.musicplayer.models.ListData
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.adapters.ListAdapter
import com.app.musicplayer.ui.holder.ListItemHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SongsAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    ListAdapter<Track>() {

    override fun onBindListItem(listItemHolder: ListItemHolder, item: Track) {
        listItemHolder.apply {
            songName = item.title
            artist = item.artist
            setDefaultImageRes(item.path)
        }
    }

    override fun convertDataToListData(items: List<Track>) =
        ListData.fromSongs(items)

}