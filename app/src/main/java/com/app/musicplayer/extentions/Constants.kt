package com.app.musicplayer.utils

import android.net.Uri
import android.os.Build
import android.os.Looper
import androidx.annotation.ChecksSdkIntAtLeast

private const val PATH = "com.app.musicplayer.action."
const val DISMISS = PATH + "DISMISS"
const val FINISH = PATH + "FINISH"
const val PREVIOUS = PATH + "PREVIOUS"
const val PAUSE = PATH + "PAUSE"
const val PLAYPAUSE = PATH + "PLAYPAUSE"
const val NEXT = PATH + "NEXT"
const val BROADCAST_STATUS = PATH + "BROADCAST_STATUS"
const val INIT_PATH = PATH + "INIT_PATH"
const val INIT = PATH + "INIT"
const val FINISH_IF_NOT_PLAYING = PATH + "FINISH_IF_NOT_PLAYING"
const val NOTIFICATION_DISMISSED = PATH + "NOTIFICATION_DISMISSED"
const val PROGRESS_CONTROLS_ACTION = PATH + "PROGRESS_CONTROLS"
const val NEXT_PREVIOUS_ACTION = PATH + "NEXT_PREVIOUS_ACTION"
const val PLAY_PAUSE_ACTION = PATH + "PLAY_PAUSE_ACTION"
const val DISMISS_PLAYER_ACTION = PATH + "dismiss_player_action"
const val SET_PROGRESS = PATH + "SET_PROGRESS"

const val DISMISS_PLAYER = "dismiss_player"
const val GET_CURRENT_POSITION = "track_position"
const val GET_TRACK_DURATION = "track_duration"
const val PLAY_PAUSE = "play_pause"
const val COMPLETE = "complete"
const val PROGRESS = "progress"
const val GENERIC_PERMISSION_HANDLER = 1

const val PERMISSION_READ_STORAGE = 2
const val PERMISSION_WRITE_STORAGE = 3
const val PERMISSION_READ_MEDIA_AUDIOS = 4
const val PERMISSION_POST_NOTIFICATIONS = 5
const val OPEN_SETTINGS = 6

const val POSITION = "position"
const val SERIALIZED_LIST = "song_list"
const val TRACK = "track"
const val RESTART_PLAYER = "RESTART_PLAYER"
const val TRACK_ID = "track_id"
const val NEXT_PREVIOUS_TRACK_ID = "next_prev_track_id"
const val TRACK_ID_SERVICE = "track_id_service"

val artworkUri = Uri.parse("content://media/external/audio/albumart")

fun getPermissionToRequest() =
    if (isTiramisuPlus()) PERMISSION_READ_MEDIA_AUDIOS else PERMISSION_WRITE_STORAGE

//check if device is running on Android 11 or higher
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

//check if device is running on Android 10 or higher
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

//check if device is running on Android 12 or higher
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isTiramisuPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

//check if device is running on Android 8 or higher (for notification)
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun ensureBackgroundThread(callback: () -> Unit) {
    if (isOnMainThread()) {
        Thread {
            callback()
        }.start()
    } else {
        callback()
    }
}