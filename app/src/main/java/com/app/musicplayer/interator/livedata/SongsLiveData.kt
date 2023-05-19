package com.app.musicplayer.interator.livedata

import android.content.Context
import com.app.musicplayer.contentresolver.SongsContentResolver
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.models.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Singleton
class SongsLiveData(
    @ApplicationContext context: Context,
    private val songId: Long? = null,
    private val contentResolverFactory: ContentResolverFactory,
) : ContentProviderLiveData<SongsContentResolver, Track>(context) {
    override val contentResolver by lazy { contentResolverFactory.getSongsContentResolver(songId) }
}