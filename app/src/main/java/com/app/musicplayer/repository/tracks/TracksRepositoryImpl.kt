package com.app.musicplayer.repository.tracks

import androidx.lifecycle.LiveData
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.di.factory.livedata.LiveDataFactory
import com.app.musicplayer.models.Track
import javax.inject.Inject

class TracksRepositoryImpl @Inject constructor(
    private val liveDataFactory: LiveDataFactory,
    private val contentResolverFactory: ContentResolverFactory
) : TracksRepository{
    override fun getTracks(): LiveData<List<Track>> = liveDataFactory.getTracksLiveData()
    override fun getTracksOfAlbum(albumId: Long?, callback: (List<Track>) -> Unit) {
        contentResolverFactory.getTracksContentResolver(null,albumId).queryItems{
            callback.invoke(it)
        }
    }
}