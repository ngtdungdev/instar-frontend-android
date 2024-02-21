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
            // Khởi tạo SharedPreferences
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

            // Lấy accessToken từ SharedPreferences
            val accessToken = sharedPreferences.getString("accessToken", null)

            if (accessToken == null) {
                // Nếu có accessToken, bạn có thể sử dụng nó cho các yêu cầu API tiếp theo ở đây
                // Ví dụ:
                val intent = Intent(this@LoadingScreenActivity, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Nếu không có accessToken, bạn có thể chuyển người dùng đến màn hình đăng nhập hoặc thực hiện các hành động khác
                // Ví dụ:
                val intent = Intent(this@LoadingScreenActivity, HomeActivity::class.java)
                startActivity(intent)
            }


            finish()
        }, 5000)
    }
}