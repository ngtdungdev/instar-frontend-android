package com.instar.frontend_android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.instar.frontend_android.ui.activities.LoadingScreenActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_load);
        super.onCreate(savedInstanceState)



        val handler = Handler(mainLooper)
        handler.postDelayed({
            val intent = Intent(this@MainActivity, LoadingScreenActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }
}