package com.instar.frontend_android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.instar.frontend_android.ui.activities.DirectMessageActivity
import com.instar.frontend_android.ui.activities.LoadingScreenActivity
import com.instar.frontend_android.ui.activities.LoginActivity
import com.instar.frontend_android.ui.activities.MainScreenActivity
import com.instar.frontend_android.ui.activities.NotificationActivity

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
            startIntentAfterMilliseconds(LoginActivity::class.java, 3000)
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
                val mainIntent = Intent(this@MainActivity, MainScreenActivity::class.java)
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                mainIntent.putExtra("position", 1)
                startActivity(mainIntent)

                val intent = Intent(this@MainActivity, NotificationActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            "like-post" -> {
                val post: String = intent.extras!!.getString("postId", "")
                val mainIntent = Intent(this@MainActivity, MainScreenActivity::class.java)
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                mainIntent.putExtra("position", 1)
                startActivity(mainIntent)

                val intent = Intent(this@MainActivity, NotificationActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            "like-comment" -> {
                val mainIntent = Intent(this@MainActivity, MainScreenActivity::class.java)
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                mainIntent.putExtra("position", 1)
                startActivity(mainIntent)

                val intent = Intent(this@MainActivity, NotificationActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            "add-comment" -> {
                val post: String = intent.extras!!.getString("postId", "")
                val mainIntent = Intent(this@MainActivity, MainScreenActivity::class.java)
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                mainIntent.putExtra("position", 1)
                startActivity(mainIntent)

                val intent = Intent(this@MainActivity, NotificationActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            "reply-comment" -> {
                val mainIntent = Intent(this@MainActivity, MainScreenActivity::class.java)
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                mainIntent.putExtra("position", 1)
                startActivity(mainIntent)

                val intent = Intent(this@MainActivity, NotificationActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
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