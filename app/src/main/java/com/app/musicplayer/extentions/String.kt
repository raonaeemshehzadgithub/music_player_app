package com.app.musicplayer.extentions

import android.content.ContentUris
import com.app.musicplayer.utils.artworkUri

fun String.getThumbnailUri(): String {
    val coverUri = ContentUris.withAppendedId(artworkUri, this.toLong())
    return coverUri.toString()
}