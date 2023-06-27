package com.app.musicplayer.contentresolver

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.app.musicplayer.core.SelectionBuilder
import com.app.musicplayer.extentions.getLongValue
import com.app.musicplayer.extentions.preventMessagesAppRecordings
import com.app.musicplayer.extentions.preventRecorderAppRecordings
import com.app.musicplayer.extentions.getStringValue
import com.app.musicplayer.models.Track

class TracksContentResolver(
    context: Context,
    private val trackId: Long? = null,
    private val albumId: Long? = null,
    private val name: String? = null
) :
    BaseContentResolver<Track>(context) {
    override val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    override val filterUri: Uri? = null

    override val selection: String
        get() {
            val selection = if (trackId != null)
                SelectionBuilder().addSelection(MediaStore.Audio.Media._ID, trackId)
            else if (albumId != null)
                SelectionBuilder().addSelection(MediaStore.Audio.Media.ALBUM_ID, albumId)
            else
                SelectionBuilder().addSelection(MediaStore.Audio.Media.DISPLAY_NAME, name)
            filter?.let { selection.addString("(${MediaStore.Audio.Media.DISPLAY_NAME} LIKE '%$filter%')") }
            selection.addString("(${MediaStore.Audio.Media.DATA} NOT LIKE '${preventMessagesAppRecordings()}%')")
            selection.addString("(${MediaStore.Audio.Media.DATA} NOT LIKE '${preventRecorderAppRecordings()}%')")
            return selection.build()
        }
    override val sortOrder: String = MediaStore.Audio.Media.DATE_ADDED + " ASC"
    override val projection: Array<String> = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM_ID
    )
    override val selectionArgs: Array<String>? = null

    override fun convertCursorToItem(cursor: Cursor) = Track(
        id = cursor.getLongValue(MediaStore.Audio.Media._ID),
        title = cursor.getStringValue(MediaStore.Audio.Media.DISPLAY_NAME) ?: "",
        artist = cursor.getStringValue(MediaStore.Audio.Media.ARTIST) ?: "",
        duration = cursor.getLongValue(MediaStore.Audio.Media.DURATION),
        path = cursor.getStringValue(MediaStore.Audio.Media.DATA) ?: "",
        album_id = cursor.getStringValue(MediaStore.Audio.Media.ALBUM_ID) ?: ""
    )
}