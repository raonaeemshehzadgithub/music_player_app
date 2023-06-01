package com.app.musicplayer.interator.tracks

import android.content.Context
import com.app.musicplayer.contentresolver.TracksContentResolver
import com.app.musicplayer.interator.base.BaseInteratorImpl
import com.app.musicplayer.models.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TracksInteractorImpl @Inject constructor(
    private val disposable: CompositeDisposable,
    @ApplicationContext private val context: Context
) : BaseInteratorImpl<TracksInteractor.Listener>(), TracksInteractor {
    override fun deleteTrack(trackId: Long) {
    }

    override fun queryTrack(trackId: Long, callback: (Track?) -> Unit) {
        disposable.add(TracksContentResolver(context, trackId).queryItems { tracks ->
            tracks.let { callback.invoke(tracks.getOrNull(0)) }
        })
    }
}