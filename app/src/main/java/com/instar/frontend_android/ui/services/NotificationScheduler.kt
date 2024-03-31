package com.instar.frontend_android.ui.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper

class NotificationScheduler: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            NotificationHelper.showNotification(context, "Thích", "quangduy201 đã thích ảnh của bạn.", 1)
        }, 10000)

    }
}