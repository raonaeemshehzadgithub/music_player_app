package com.app.musicplayer.interator.player

import android.content.Context
import com.app.musicplayer.helpers.RingtoneManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlayerInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayerInteractor {
    override fun deleteTrack(trackId: Long) {
    }

    override fun setPhoneRingtone(context: Context, trackId: Long) {
        if (RingtoneManager.requiresDialog(context)) {
            RingtoneManager.showDialog(context)
        } else {
            RingtoneManager.setRingtone(context, trackId)
        }
    }
}