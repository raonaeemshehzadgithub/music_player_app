package com.app.musicplayer.ui.adapters

import android.content.Context
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.models.ListData
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.holder.ListItemHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TracksAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    ListAdapter<Track>() {

    override fun onBindListItem(listItemHolder: ListItemHolder, item: Track) {
        listItemHolder.apply {
            isMenuIconShown(true)
            isListItemShown(true)
            isArtistItemShown(false)
            trackName = item.title ?: ""
            artist = item.artist?.isUnknownString() ?: ""

            setTrackThumbnail(item.album_id?.getThumbnailUri() ?: "")
        }
    }

    override fun convertDataToListData(items: List<Track>) =
        ListData.fromTracks(items)

}