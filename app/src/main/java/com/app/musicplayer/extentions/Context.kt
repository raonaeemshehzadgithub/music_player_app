package com.app.musicplayer.extentions

import java.util.*

fun createWaveform(): IntArray {
    val random = Random(System.currentTimeMillis())
    val length = 35
    val values = IntArray(length)
    var maxValue = 0
    for (i in 0 until length) {
        val newValue: Int = 5 + random.nextInt(35)
        if (newValue > maxValue) {
            maxValue = newValue
        }
        values[i] = newValue
    }
    return values
}