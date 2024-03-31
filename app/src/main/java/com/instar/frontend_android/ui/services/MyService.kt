package com.instar.frontend_android.ui.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class MyService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            NotificationHelper.showNotification(this, "Thích", "quangduy201 đã thích ảnh của bạn.", 1)
        }, 10000)
        NotificationScheduler()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
