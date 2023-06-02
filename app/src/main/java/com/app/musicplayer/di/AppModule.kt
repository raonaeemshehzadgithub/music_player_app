package com.app.musicplayer.di

import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactoryImpl
import com.app.musicplayer.di.factory.livedata.LiveDataFactory
import com.app.musicplayer.di.factory.livedata.LiveDataFactoryImpl
import com.app.musicplayer.interator.albums.AlbumsInteractorImpl
import com.app.musicplayer.interator.albums.AlbumsInterator
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.interator.tracks.TracksInteractorImpl
import com.app.musicplayer.interator.string.StringsInteractor
import com.app.musicplayer.interator.string.StringsInteratorImpl
import com.app.musicplayer.repository.albums.AlbumsRepository
import com.app.musicplayer.repository.albums.AlbumsRepositoryImpl
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.repository.tracks.TracksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.disposables.CompositeDisposable

@Module(includes = [AppModule.BindModule::class])
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideDisposable(): CompositeDisposable = CompositeDisposable()

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindModule {

        @Binds
        fun bindsTracksInteractor(tracksInteractorImpl: TracksInteractorImpl): TracksInteractor

        @Binds
        fun bindsAlbumsInteractor(albumsInteractorImpl: AlbumsInteractorImpl): AlbumsInterator

        @Binds
        fun bindsContentResolverFactory(contentResolverFactoryImpl: ContentResolverFactoryImpl): ContentResolverFactory

        @Binds
        fun bindsTracksRepository(tracksRepositoryImpl: TracksRepositoryImpl): TracksRepository

        @Binds
        fun bindsAlbumsRepository(albumsRepositoryImpl: AlbumsRepositoryImpl): AlbumsRepository

        @Binds
        fun bindsLiveDataFactory(liveDataFactoryImpl: LiveDataFactoryImpl): LiveDataFactory

        @Binds
        fun bindsStringsInterator(stringInteractorImpl: StringsInteratorImpl): StringsInteractor
    }
}