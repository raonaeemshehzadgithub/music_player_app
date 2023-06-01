package com.app.musicplayer.contentresolver

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.app.musicplayer.core.SelectionBuilder
import com.app.musicplayer.extentions.getLongValue
import com.app.musicplayer.extentions.getStringValue
import com.app.musicplayer.models.Track

class TracksContentResolver(context: Context, trackId: Long? = null) :
    BaseContentResolver<Track>(context) {
    override val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    override val selection by lazy {
        SelectionBuilder()
            .addNotNull(MediaStore.Audio.Media.DISPLAY_NAME)
            .addSelection(MediaStore.Audio.Media._ID, trackId)
            .build()
    }
    override val sortOrder: String = MediaStore.Audio.Media.DATE_ADDED + " ASC"
    override val projection: Array<String> = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.BUCKET_DISPLAY_NAME
    )
    override val selectionArgs: Array<String>? = null

    override fun convertCursorToItem(cursor: Cursor) = Track(
        id = cursor.getLongValue(MediaStore.Audio.Media._ID),
        title = cursor.getStringValue(MediaStore.Audio.Media.DISPLAY_NAME),
        artist = cursor.getStringValue(MediaStore.Audio.Media.ARTIST),
        duration = cursor.getLongValue(MediaStore.Audio.Media.DURATION),
        path = cursor.getStringValue(MediaStore.Audio.Media.DATA),
        thumbnail = cursor.getStringValue(MediaStore.Audio.Media.DATA),
        album_id = cursor.getStringValue(MediaStore.Audio.Media.ALBUM_ID),
        folderName = cursor.getStringValue(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
    )
}