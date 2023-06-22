package com.app.musicplayer.models

data class Track(
    val id: Long?=null,
    val title: String? = null,
    val artist: String? = null,
    val duration: Long? = null,
    val path: String? = null,
    var album_id: String? = null
) : java.io.Serializable
