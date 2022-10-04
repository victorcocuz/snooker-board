package com.quickpoint.snookerboard.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.quickpoint.snookerboard.R

// Notification ID.
private const val NOTIFICATION_ID = 0
//private val REQUEST_CODE = 0
//private val FLAGS = 0

// This was used as part of a project submission. Not needed at the moment
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.game_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_ball_red)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)

    notify(NOTIFICATION_ID, builder.build())
}

fun createChannel(channelId: String, channelName: String, activity: Activity) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationChannel.apply {
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = "Game was played"
        }

        val notificationManager = activity.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

fun setupGameNotification(activity: Activity) = activity.application.apply {
    createChannel(
        getString(R.string.game_notification_channel_id),
        getString(R.string.game_notification_channel_name),
        activity
    )

    val notificationManager = ContextCompat.getSystemService(
        this,
        NotificationManager::class.java
    ) as NotificationManager
    notificationManager.sendNotification(getString(R.string.notification_text), activity)
}