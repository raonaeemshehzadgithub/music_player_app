package com.app.musicplayer.ui.adapters

import android.content.Context
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.models.ListData
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.holder.ListItemHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FavoritesAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    ListAdapter<Track>() {
    override fun onBindListItem(listItemHolder: ListItemHolder, item: Track) {
        listItemHolder.apply {
            isMenuIconShown(true)
            isListItemShown(true)
            isArtistItemShown(false)
            isFavoriteIconShown(true)
            trackName = item.title ?: ""
            artist = item.artist?.isUnknownString() ?: ""

            setTrackThumbnail(item.albumId?.getThumbnailUri() ?: "")
        }
    }

    override fun convertDataToListData(items: List<Track>) =
        ListData.fromFavoriteTracks(items)

}