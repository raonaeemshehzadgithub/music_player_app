package com.app.musicplayer.extentions

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.app.musicplayer.R

fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) beInvisible() else beVisible()

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

fun View.beInvisible() { visibility = View.INVISIBLE }

fun View.beVisible() { visibility = View.VISIBLE }

fun View.beGone() { visibility = View.GONE }

fun ImageView.setSelectedTint(context:Context){
    this.setColorFilter(ContextCompat.getColor(context, R.color.purple))
}

fun ImageView.setUnSelectedTint(context:Context){
    if (context.isDarkMode()) {
        this.setColorFilter(ContextCompat.getColor(context, R.color.white))
    } else {
        this.setColorFilter(ContextCompat.getColor(context, R.color.black))
    }
}