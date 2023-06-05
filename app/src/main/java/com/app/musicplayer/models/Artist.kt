package com.app.musicplayer.models

data class Artist(
    val id: Long,
    val artistTitle: String? = null,
    val tracksCount: String? = null,
    val albumsCount: String? = null,
    val albumId: Long? = null
)