package com.instar.frontend_android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.instar.frontend_android.ui.activities.LoadingScreenActivity
import com.instar.frontend_android.ui.activities.MainScreenActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_load);
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", "")



        val handler = Handler(mainLooper)
        handler.postDelayed({
            if (accessToken != null) {
                if (accessToken.isNotEmpty()) {
                    val intent = Intent(this@MainActivity, MainScreenActivity::class.java)
                    startActivity(intent)

                    finish()
                }
            }

            val intent = Intent(this@MainActivity, LoadingScreenActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}