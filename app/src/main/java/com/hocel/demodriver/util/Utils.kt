package com.hocel.demodriver.util

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hocel.demodriver.R
import com.hocel.demodriver.util.Constants.OVERLAY_NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Context.isServiceRunningInForeground(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    manager.getRunningServices(Int.MAX_VALUE).forEach { service ->
        if (serviceClass.name == service.service.className) {
            if (service.foreground) {
                return true
            }
        }
    }
    return false
}
@RequiresApi(Build.VERSION_CODES.O)
fun NotificationManager.createNotificationChannelIfNotExist(
    channelId: String,
    channelName: String,
    importance: Int = NotificationManager.IMPORTANCE_HIGH
) {
    var channel = this.getNotificationChannel(channelId)

    if (channel == null) {
        channel = NotificationChannel(
            channelId,
            channelName,
            importance
        )
        this.createNotificationChannel(channel)
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun pushRequestNotification(
    context: Context,
    channelId: String = "ya_demo_driver",
    channelName: String = "YaDemoDriver",
    title: String,
    description: String? = null,
    intent: Intent
) {
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.mipmap.ic_logo)
        .setContentTitle(title)
        .setContentText(description)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(NotificationCompat.CATEGORY_CALL)
        .setGroup("trip_notification")
        .setStyle(NotificationCompat.BigTextStyle().bigText(description))
        .setFullScreenIntent(pendingIntent, true)
    if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT)
        notificationManager.createPushRequestNotification(channelId, channelName)
    notificationManager.notify(OVERLAY_NOTIFICATION_ID, notificationBuilder.build())
}

@RequiresApi(Build.VERSION_CODES.O)
fun NotificationManager.createPushRequestNotification(
    channelId: String,
    channelName: String,
    importance: Int = NotificationManager.IMPORTANCE_HIGH
) {
    var channel = this.getNotificationChannel(channelId)

    if (channel == null) {
        channel = NotificationChannel(
            channelId,
            channelName,
            importance
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        this.createNotificationChannel(channel)
    }
}
