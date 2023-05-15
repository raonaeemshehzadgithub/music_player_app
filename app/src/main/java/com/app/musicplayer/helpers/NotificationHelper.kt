package com.app.musicplayer.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.musicplayer.R
import com.app.musicplayer.extentions.notificationManager
import com.app.musicplayer.models.Track
import com.app.musicplayer.receivers.ControlActionsListener
import com.app.musicplayer.receivers.NotificationDismissedReceiver
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.ui.activities.MainActivity
import com.app.musicplayer.utils.*

class NotificationHelper(
    private val context: Context,
    private val mediaSessionToken: MediaSessionCompat.Token
) {

    private var notificationManager = context.notificationManager

    @RequiresApi(Build.VERSION_CODES.M)
    fun createPlayerNotification(
        track: Track,
        isPlaying: Boolean,
        largeIcon: Bitmap?,
        callback: (Notification) -> Unit
    ) {

        var postTime = 0L
        var showWhen = false
        var usesChronometer = false
        var ongoing = false
        if (isPlaying) {
            postTime = System.currentTimeMillis() - (MusicService.mPlayer?.currentPosition ?: 0)
            showWhen = true
            usesChronometer = true
            ongoing = true
        }

        val notificationDismissedIntent =
            Intent(context, NotificationDismissedReceiver::class.java).apply {
                action = NOTIFICATION_DISMISSED
            }
        val flag = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val notificationDismissedPendingIntent =
            PendingIntent.getBroadcast(context, 0, notificationDismissedIntent, flag)

        val previousAction = NotificationCompat.Action.Builder(
            R.drawable.ic_previous,
            context.getString(R.string.previous),
            getIntent(PREVIOUS)
        ).build()
        val nextAction = NotificationCompat.Action.Builder(
            R.drawable.ic_next,
            context.getString(R.string.next),
            getIntent(NEXT)
        ).build()
        val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val playPauseAction = NotificationCompat.Action.Builder(
            playPauseIcon,
            context.getString(R.string.playpause),
            getIntent(PLAYPAUSE)
        ).build()
        val dismissAction = NotificationCompat.Action.Builder(
            R.drawable.ic_cross,
            context.getString(R.string.dismiss),
            getIntent(DISMISS)
        ).build()

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setContentTitle(track.title)
            .setContentText(track.artist)
            .setSmallIcon(R.drawable.ic_music)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setWhen(postTime)
            .setShowWhen(showWhen)
            .setUsesChronometer(usesChronometer)
            .setContentIntent(getContentIntent())
            .setOngoing(ongoing)
            .setChannelId(NOTIFICATION_CHANNEL)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
//                    .setMediaSession(mediaSessionToken)
            )
            .setDeleteIntent(notificationDismissedPendingIntent)
            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .addAction(dismissAction)
        try {
            builder.setLargeIcon(largeIcon)
        } catch (ignored: OutOfMemoryError) {
        }
        callback(builder.build())
    }

    fun notify(id: Int, notification: Notification) = notificationManager.notify(id, notification)

    fun cancel(id: Int) = notificationManager.cancel(id)

    private fun getContentIntent(): PendingIntent {
        val contentIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getIntent(action: String): PendingIntent {
        val intent = Intent(context, ControlActionsListener::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL = "music_channel"
        const val NOTIFICATION_ID = 12

        @RequiresApi(Build.VERSION_CODES.O)
        private fun createNotificationChannel(
            context: Context,
            notificationManager: NotificationManager
        ) {
            var notificationChannel: NotificationChannel? =
                notificationManager.getNotificationChannel(
                    NOTIFICATION_CHANNEL
                )
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationChannel.enableLights(false)
                notificationChannel.enableVibration(false)
                notificationChannel.setShowBadge(false)

                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        fun createInstance(context: Context, mediaSession: MediaSessionCompat): NotificationHelper {
            if (isOreoPlus()) {
                createNotificationChannel(context, context.notificationManager)
            }
            return NotificationHelper(context, mediaSession.sessionToken)
        }
    }
}