package com.example.snookerscore.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.snookerscore.R

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.game_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ball_red)
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

fun setupGameNotification(activity: Activity) {
    val application = activity.application
    createChannel(
        application.getString(R.string.game_notification_channel_id),
        application.getString(R.string.game_notification_channel_name),
        activity
    )

    val notificationManager = ContextCompat.getSystemService(
        application,
        NotificationManager::class.java
    ) as NotificationManager
    notificationManager.sendNotification(application.getString(R.string.notification_text), activity)
}