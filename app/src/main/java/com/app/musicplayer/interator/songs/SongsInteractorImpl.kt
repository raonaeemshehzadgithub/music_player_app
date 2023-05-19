package com.app.musicplayer.interator.songs

import android.content.Context
import com.app.musicplayer.contentresolver.SongsContentResolver
import com.app.musicplayer.interator.base.BaseInteratorImpl
import com.app.musicplayer.models.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsInteractorImpl @Inject constructor(
    private val disposable: CompositeDisposable,
    @ApplicationContext private val context: Context
) : BaseInteratorImpl<SongsInteractor.Listener>(), SongsInteractor {
    override fun deleteSong(songId: Long) {
    }

    override fun querySong(songId: Long, callback: (Track?) -> Unit) {
        disposable.add(SongsContentResolver(context, songId).queryItems { songs ->
            songs.let { callback.invoke(songs.getOrNull(0)) }
        })
    }
}