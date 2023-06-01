package com.app.musicplayer.di.factory.contentresolver

import com.app.musicplayer.contentresolver.TracksContentResolver

interface ContentResolverFactory {
    fun getTracksContentResolver(trackId: Long? = null) : TracksContentResolver
}