package com.app.musicplayer.di.factory.livedata

import android.content.Context
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.interator.livedata.SongsLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveDataFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentResolverFactory: ContentResolverFactory
) : LiveDataFactory {
    override fun getSongsLiveData(songId: Long?): SongsLiveData =
        SongsLiveData(context, songId,contentResolverFactory)
}