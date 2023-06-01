package com.app.musicplayer.di.factory.contentresolver

import android.content.Context
import com.app.musicplayer.contentresolver.TracksContentResolver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContentResolverFactoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    ContentResolverFactory {
    override fun getTracksContentResolver(trackId: Long?): TracksContentResolver =
        TracksContentResolver(context,trackId)
}