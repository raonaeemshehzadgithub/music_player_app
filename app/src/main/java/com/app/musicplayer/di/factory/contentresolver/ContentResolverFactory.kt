package com.app.musicplayer.di.factory.contentresolver

import com.app.musicplayer.contentresolver.AlbumsContentResolver
import com.app.musicplayer.contentresolver.TracksContentResolver

interface ContentResolverFactory {
    fun getTracksContentResolver(trackId: Long? = null) : TracksContentResolver
    fun getAlbumsContentResolver(albumId: Long? = null) : AlbumsContentResolver
}