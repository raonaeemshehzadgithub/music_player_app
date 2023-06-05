package com.app.musicplayer.ui.adapters

import android.content.Context
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.models.Artist
import com.app.musicplayer.ui.holder.ListItemHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ArtistsAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    ListAdapter<Artist>() {
    override fun onBindListItem(listItemHolder: ListItemHolder, item: Artist) {
        listItemHolder.apply {
            isListItemShown(false)
            isArtistItemShown(false)
            artistTitle = item.artistTitle ?: ""

            setArtistThumbnail(item.albumId.toString().getThumbnailUri())
        }
    }
}