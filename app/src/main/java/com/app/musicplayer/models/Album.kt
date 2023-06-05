package com.app.musicplayer.models

data class Album(
    val id: Long,
    val albumId: Long? = null,
    val albumTitle: String? = null,
    val trackCount: String? = null,
    val artist: String? = null
)