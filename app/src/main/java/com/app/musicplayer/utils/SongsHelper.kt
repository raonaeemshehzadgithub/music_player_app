package com.app.musicplayer.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import com.app.musicplayer.models.Song

class SongsHelper {
    @SuppressLint("Range")
    fun retrieveAllSongs(context: Context): ArrayList<Song> {
        val audioList = ArrayList<Song>()

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val cursor = context.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val duration =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val thumbnail = getAlbumArt(context, id)
                val audio = Song(id, title, artist, duration, data, thumbnail)
                audioList.add(audio)
            }
            cursor.close()
        }
        return audioList
    }
    @SuppressLint("Range")
    fun getAlbumArt(context: Context, audioId: Long): Bitmap? {
        val projection = arrayOf(MediaStore.Audio.Media.ALBUM_ID)
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(audioId.toString())
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        var albumId: Long = 0
        if (cursor != null && cursor.moveToFirst()) {
            albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
            cursor.close()
        }

        val bitmap: Bitmap?
        if (albumId > 0) {
            val uri =
                ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
            val albumCursor = context.contentResolver.query(
                uri,
                arrayOf(MediaStore.Audio.Albums.ALBUM_ART),
                null,
                null,
                null
            )
            if (albumCursor != null && albumCursor.moveToFirst()) {
                val albumArt =
                    albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
                bitmap = BitmapFactory.decodeFile(albumArt)
                albumCursor.close()
                return bitmap
            }
        }

        return null
    }
}