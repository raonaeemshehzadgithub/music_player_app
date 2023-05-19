package com.app.musicplayer.repository.songs

import androidx.lifecycle.LiveData
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.di.factory.livedata.LiveDataFactory
import com.app.musicplayer.models.Track
import javax.inject.Inject

class SongsRepositoryImpl @Inject constructor(
    private val liveDataFactory: LiveDataFactory,
    private val contentResolverFactory: ContentResolverFactory
) : SongsRepository{
    override fun getSongs(): LiveData<List<Track>> = liveDataFactory.getSongsLiveData()
}