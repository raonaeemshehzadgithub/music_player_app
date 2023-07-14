package com.app.musicplayer.ui.adapters

import android.content.Context
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.models.ListData
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.ui.holder.ListItemHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RecentTracksAdapter @Inject constructor(@ApplicationContext private val context: Context):
    ListAdapter<RecentTrackEntity>() {
    override fun onBindListItem(listItemHolder: ListItemHolder, item: RecentTrackEntity) {
        listItemHolder.apply {
            isMenuIconShown(true)
            isListItemShown(true)
            isArtistItemShown(false)
            trackName = item.title ?: ""
            artist = item.artist?.isUnknownString() ?: ""

            setTrackThumbnail(item.albumId?.getThumbnailUri() ?: "")
        }
    }

    override fun convertDataToListData(items: List<RecentTrackEntity>) =
        ListData.fromRecentTracks(items)

}