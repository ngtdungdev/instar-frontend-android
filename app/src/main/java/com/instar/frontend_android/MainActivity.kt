package com.instar.frontend_android

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.instar.frontend_android.ui.activities.DirectMessageActivity
import com.instar.frontend_android.ui.activities.LoadingScreenActivity
import com.instar.frontend_android.ui.activities.LoginActivity
import com.instar.frontend_android.ui.activities.LoginOtherActivity
import com.instar.frontend_android.ui.activities.MainScreenActivity
import com.instar.frontend_android.ui.services.FCMService
import com.instar.frontend_android.ui.services.FacebookService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load);

        // The application is opened normally
        if (intent.extras == null) {
            startIntentAfterMilliseconds(LoadingScreenActivity::class.java, 3000)
            return
        }

        // The application is opened from notification
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", "")

        // If user is not logged in
        if (accessToken.isNullOrEmpty()) {
            if (FacebookService.isLoggedIn())
                startIntentAfterMilliseconds(LoginActivity::class.java, 3000)
            else
                startIntentAfterMilliseconds(LoginOtherActivity::class.java, 3000)
            return
        }

        // Read the extras from the notification
        val userId: String = intent.extras!!.getString("userId", "")
        val type: String = intent.extras!!.getString("type", "")
        when (type) {
            "message" -> {
                val mainIntent = Intent(this@MainActivity, MainScreenActivity::class.java)
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                mainIntent.putExtra("position", 2)
                startActivity(mainIntent)

                val chatId: String = intent.extras!!.getString("chatId", "")
                val intent = Intent(this@MainActivity, DirectMessageActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("chatID", chatId)
                startActivity(intent)
            }
            "follow" -> {
                TODO("Not yet implemented.")
            }
            "like-post" -> {
                val post: String = intent.extras!!.getString("postId", "")
                TODO("Not yet implemented.")
            }
            "like-comment" -> {
                TODO("Not yet implemented.")
            }
            "add-comment" -> {
                val post: String = intent.extras!!.getString("postId", "")
                TODO("Not yet implemented.")
            }
            "reply-comment" -> {
                TODO("Not yet implemented.")
            }
            else -> {

            }
        }
        finish()
    }

    private fun startIntentAfterMilliseconds(cls: Class<*>, delayMillis: Long, isFinished: Boolean = true) {
        val intent = Intent(this@MainActivity, cls)
        startIntentAfterMilliseconds(intent, delayMillis, isFinished)
    }

    private fun startIntentAfterMilliseconds(intent: Intent, delayMillis: Long, isFinished: Boolean = true) {
        startDoingAfterMilliseconds({
            startActivity(intent)
            if (isFinished)
                finish()
        }, delayMillis)
    }

    private fun startDoingAfterMilliseconds(code: Runnable, delayMillis: Long) {
        val handler = Handler(mainLooper)
        handler.postDelayed(code, delayMillis)
    }
}