package com.app.musicplayer.models

import android.graphics.Bitmap

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val data: String,
    var thumbnail: Bitmap?
):java.io.Serializable
