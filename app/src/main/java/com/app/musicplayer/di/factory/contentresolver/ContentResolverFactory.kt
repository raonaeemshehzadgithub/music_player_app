package com.app.musicplayer.di.factory.contentresolver

import com.app.musicplayer.contentresolver.SongsContentResolver

interface ContentResolverFactory {
    fun getSongsContentResolver(contactId: Long? = null) : SongsContentResolver
}