package com.instar.frontend_android.ui.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.instar.frontend_android.MainActivity
import com.instar.frontend_android.R

object NotificationHelper {
    private const val CHANNEL_ID = "com.instar.frontend_android"
    private const val CHANNEL_NAME = "Instar"

    fun showNotification(context: Context, title: String, message: String, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
            importance = NotificationManager.IMPORTANCE_DEFAULT
        }
        notificationManager.createNotificationChannel(channel)
        val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.no1)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_instagram_icon_black_white)
            .setContentTitle(title)
            .setContentText(message)
            .setLargeIcon(bitmap)
//                .setStyle(NotificationCompat.BigPictureStyle()
//                          .bigPicture(bitmap)
//                          .bigLargeIcon(null))
            .setColor(ContextCompat.getColor(context, R.color.white))
            .setColorized(true)
            .setAutoCancel(true)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent)
        notificationManager.notify(id, builder.build())
    }
}
