package com.app.musicplayer.models

data class Track(
    val id: Long,
    val title: String,
    val artist: String?,
    val duration: Long,
    val path: String,
    var thumbnail: String,
    var album_id: String,
    var folderName:String
):java.io.Serializable
