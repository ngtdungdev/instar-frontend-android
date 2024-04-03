package com.instar.frontend_android.ui.services

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.instar.frontend_android.ui.utils.Helpers

class FCMService : FirebaseMessagingService {
    private var applicationContext: Context = this
    private var notificationService: NotificationService = ServiceBuilder.buildService(NotificationService::class.java, applicationContext)

    constructor()

    constructor(applicationContext: Context) : this() {
        this.applicationContext = applicationContext
        this.notificationService = ServiceBuilder.buildService(NotificationService::class.java, applicationContext)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title: String = remoteMessage.notification?.title.toString()
        val message: String = remoteMessage.notification?.body.toString()
        val notificationId = 1

        NotificationHelper.showNotification(this, title, message, notificationId);
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        println("Refreshed token: $token")
        val userId: String? = Helpers.getUserId(applicationContext)
        if (userId != null) {
            NotificationService.createNewToken(userId, token)
        }
    }

    fun getFirebaseCloudMessagingToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result

                println(token)
                val userId: String? = Helpers.getUserId(applicationContext)
                if (userId != null) {
                    NotificationService.createNewToken(userId, token)
                }
            }
        }
    }
}