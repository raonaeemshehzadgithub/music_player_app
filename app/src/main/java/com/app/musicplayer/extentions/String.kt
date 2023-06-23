package com.app.musicplayer.extentions

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.app.musicplayer.utils.artworkUri
import java.io.File

fun String.getThumbnailUri(): String {
    val coverUri = ContentUris.withAppendedId(artworkUri, this.toLong())
    return coverUri.toString()
}

fun String.isUnknownString():String{
    if (this == MediaStore.UNKNOWN_STRING) {
        return this.substringAfterLast("<").substringBeforeLast(">")
    }
    return this
}

fun String.shareTrack(context: Context) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "audio/*"
    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(this))
    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this audio file!")
    context.startActivity(Intent.createChooser(shareIntent, "Share Track"))
}
fun preventMessagesAppRecordings():String {
    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    val folderPath = File(musicDir, "Messenger/Recorded").absolutePath
    return folderPath
}
fun preventRecorderAppRecordings():String {
    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    val folderPath = File(musicDir, "Recordings").absolutePath
    return folderPath
}