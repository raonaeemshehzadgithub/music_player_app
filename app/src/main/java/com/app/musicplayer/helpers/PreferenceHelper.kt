package com.app.musicplayer.helpers

import android.content.Context
import android.content.SharedPreferences
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.REPEAT_TRACK
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceHelper @Inject constructor(@ApplicationContext private val context: Context) {
    private val appPrefs: SharedPreferences =
        context.getSharedPreferences("music_player_pref", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = appPrefs.edit()

    object PreferenceVariable {
        const val REPEAT_TRACK = "repeat_track"
    }

    init {
        editor.apply()
    }

    var repeatTrack: String?
        get() = appPrefs.getString(REPEAT_TRACK, "REPEAT_TRACK")
        set(repeatTrack) {
            editor.putString(REPEAT_TRACK, repeatTrack)
            editor.apply()
        }

    fun clearPreference() {
        editor.clear()
        editor.apply()
    }
}