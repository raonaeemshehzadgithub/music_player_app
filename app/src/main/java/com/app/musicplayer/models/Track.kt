package com.app.musicplayer.models

import android.graphics.Bitmap

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val path: String,
    var thumbnail: String,
    var album: String,
    var folderName:String
):java.io.Serializable
