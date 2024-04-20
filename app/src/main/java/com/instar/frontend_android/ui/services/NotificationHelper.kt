package com.instar.frontend_android.ui.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.instar.frontend_android.MainActivity
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object NotificationHelper {
    private const val CHANNEL_ID = "com.instar.frontend_android"
    private const val CHANNEL_NAME = "Instar"

    fun showNotification(context: Context, title: String, message: String, id: Int, data: Map<String, String> = HashMap(), user: User? = null) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
            importance = NotificationManager.IMPORTANCE_DEFAULT
        }

        notificationManager.createNotificationChannel(channel)

        GlobalScope.launch(Dispatchers.IO) {
            var bitmap: Bitmap? = null
            try {
                user?.profilePicture?.url?.let {
                    val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                        .asBitmap()
                        .load(it)
                        .submit()
                    bitmap = futureTarget.get()
                }
            } catch (e: Exception) {
                Log.e("NotificationHelper", "Failed to load image from URL: ${user?.profilePicture?.url}", e)
            }

            launch(Dispatchers.Main) {
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_instagram_icon_black_white)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(bitmap)
                    .setColor(ContextCompat.getColor(context, R.color.white))
                    .setColorized(true)
                    .setAutoCancel(true)

                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    for (extra in data)
                        putExtra(extra.key, extra.value)
                }
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                builder.setContentIntent(pendingIntent)
                notificationManager.notify(id, builder.build())
            }
        }
    }
}
