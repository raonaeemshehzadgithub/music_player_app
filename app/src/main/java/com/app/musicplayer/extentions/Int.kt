package com.app.musicplayer.extentions

import com.app.musicplayer.services.MusicService
import java.util.*

fun Int.getFormattedDuration(forceShowHours: Boolean = false): String {
    val sb = StringBuilder(8)
    val hours = this / 3600
    val minutes = this % 3600 / 60
    val seconds = this % 60

    if (this >= 3600) {
        sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")
    } else if (forceShowHours) {
        sb.append("0:")
    }

    sb.append(String.format(Locale.getDefault(), "%02d", minutes))
    sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))
    return sb.toString()
}

fun Int.shuffleTrack(): Int {
    val shufflePosition = mutableListOf<Int>().apply {
        addAll(0 until this@shuffleTrack)
        shuffle()
    }
    return shufflePosition.indexOf(MusicService.positionTrack)
}