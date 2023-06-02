package com.app.musicplayer.ui.adapters

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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
            trackName = item.albumTitle
            artist = "${getTrackCountInAlbum(item.albumId)} listed"

            setDefaultAlbumRes()
        }
    }
    override fun convertDataToListData(items: List<Album>) =
        ListData.fromAlbums(items)

    private fun getTrackCountInAlbum(albumId: Long): Int {
        val albumUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val selection = "${MediaStore.Audio.Media.ALBUM_ID} = ?"
        val selectionArgs = arrayOf(albumId.toString())
        val sortOrder: String? = null

        var trackCount = 0
        context.contentResolver.query(albumUri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            trackCount = cursor.count
        }

        return trackCount
    }
}