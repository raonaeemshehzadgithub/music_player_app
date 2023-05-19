package com.app.musicplayer.di

import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactoryImpl
import com.app.musicplayer.di.factory.livedata.LiveDataFactory
import com.app.musicplayer.di.factory.livedata.LiveDataFactoryImpl
import com.app.musicplayer.interator.songs.SongsInteractor
import com.app.musicplayer.interator.songs.SongsInteractorImpl
import com.app.musicplayer.interator.string.StringsInteractor
import com.app.musicplayer.interator.string.StringsInteratorImpl
import com.app.musicplayer.repository.songs.SongsRepository
import com.app.musicplayer.repository.songs.SongsRepositoryImpl
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
        fun bindsSongsInteractor(songsInteractorImpl: SongsInteractorImpl): SongsInteractor

        @Binds
        fun bindsContentResolverFactory(contentResolverFactoryImpl: ContentResolverFactoryImpl): ContentResolverFactory

        @Binds
        fun bindsSongsRepository(songsRepositoryImpl: SongsRepositoryImpl): SongsRepository

        @Binds
        fun bindsLiveDataFactory(liveDataFactoryImpl: LiveDataFactoryImpl): LiveDataFactory

        @Binds
        fun bindsStringsInterator(stringInteractorImpl: StringsInteratorImpl): StringsInteractor
    }
}