package com.app.musicplayer.extentions

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.app.musicplayer.utils.artworkUri
import java.io.File

fun String.getThumbnailUri(): String {
    val coverUri = ContentUris.withAppendedId(artworkUri, this.toLong())
    return coverUri.toString()
}

fun String.isUnknownString(): String {
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

fun excludeMessagesAppRecordings(): String {
    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    return File(musicDir, "Messenger/Recorded").absolutePath
}

fun excludeRecorderAppRecordings(): String {
    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    return File(musicDir, "Recordings").absolutePath
}

fun String.setDefaultAlarmTone(context: Context) {
    RingtoneManager.setActualDefaultRingtoneUri(
        context,
        RingtoneManager.TYPE_ALARM,
        Uri.parse(this)
    )
    context.toast("Set default alarm tone")
}