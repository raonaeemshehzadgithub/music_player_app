package com.app.musicplayer.ui.adapters

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.models.Album
import com.app.musicplayer.models.ListData
import com.app.musicplayer.ui.holder.ListItemHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlbumsAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    ListAdapter<Album>() {

    override fun onBindListItem(listItemHolder: ListItemHolder, item: Album) {
        listItemHolder.apply {
            isFavoriteIconShown(false)
            isListItemShown(true)
            isArtistItemShown(false)
            trackName = item.albumTitle
            artist = "${item.trackCount} listed"

            setAlbumThumbnail(item.albumId.toString().getThumbnailUri())
        }
    }

    override fun convertDataToListData(items: List<Album>) =
        ListData.fromAlbums(items)
}