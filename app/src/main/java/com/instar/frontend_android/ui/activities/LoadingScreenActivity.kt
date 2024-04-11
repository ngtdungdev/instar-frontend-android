package com.instar.frontend_android.ui.activities


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Circle
import com.github.ybq.android.spinkit.style.FadingCircle
import com.instar.frontend_android.databinding.ActivityLoadingScreenBinding
import com.instar.frontend_android.ui.services.FacebookService


class LoadingScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoadingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val doubleBounce1: Sprite = FadingCircle()
        binding.spinKit1.setIndeterminateDrawable(doubleBounce1)

        val doubleBounce2: Sprite = Circle()
        binding.spinKit2.setIndeterminateDrawable(doubleBounce2)

        val handler = Handler(mainLooper)

        handler.postDelayed({
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val accessToken = sharedPreferences.getString("accessToken", null)

            var activity: Class<*> = MainScreenActivity::class.java
            if (accessToken == null) {
                activity = if (FacebookService.isLoggedIn())
                    LoginActivity::class.java
                else
                    LoginOtherActivity::class.java
            }

            val intent = Intent(this@LoadingScreenActivity, activity)
            startActivity(intent)
            finish()
        }, 5000)
    }
}