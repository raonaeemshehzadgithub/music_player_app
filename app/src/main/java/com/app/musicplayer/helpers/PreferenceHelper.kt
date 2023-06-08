package com.app.musicplayer.helpers

import android.content.Context
import android.content.SharedPreferences
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.REPEAT_TRACK
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.SHUFFLE_TRACK
import com.app.musicplayer.utils.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceHelper @Inject constructor(@ApplicationContext private val context: Context) {
    private val appPrefs: SharedPreferences =
        context.getSharedPreferences("music_player_pref", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = appPrefs.edit()

    object PreferenceVariable {
        const val REPEAT_TRACK = "repeat_track"
        const val SHUFFLE_TRACK = "shuffle_track"
    }

    init {
        editor.apply()
    }

    var repeatTrack: String?
        get() = appPrefs.getString(REPEAT_TRACK, REPEAT_TRACK_OFF)
        set(repeatTrack) {
            editor.putString(REPEAT_TRACK, repeatTrack)
            editor.apply()
        }

    var shuffleTrack: String?
        get() = appPrefs.getString(SHUFFLE_TRACK, SHUFFLE_TRACK_OFF)
        set(shuffleTrack) {
            editor.putString(SHUFFLE_TRACK, shuffleTrack)
            editor.apply()
        }

    fun clearPreference() {
        editor.clear()
        editor.apply()
    }
}