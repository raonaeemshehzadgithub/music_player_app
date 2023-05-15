package com.app.musicplayer.utils

import android.os.Build
import android.os.Looper
import androidx.annotation.ChecksSdkIntAtLeast

object Constants{

    const val GENERIC_PERMISSION_HANDLER = 1

    const val PERMISSION_READ_STORAGE = 2
    const val PERMISSION_WRITE_STORAGE = 3
    const val PERMISSION_READ_MEDIA_AUDIOS = 4
    const val OPEN_SETTINGS = 5

    const val POSITION = "position"
    const val SERIALIZED_LIST = "song_list"

    fun getPermissionToRequest() =
        if (isTiramisuPlus()) PERMISSION_READ_MEDIA_AUDIOS else PERMISSION_WRITE_STORAGE

    //check if device is running on Android 10 or higher
    fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    //check if device is running on Android 12 or higher
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    fun isTiramisuPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()
}