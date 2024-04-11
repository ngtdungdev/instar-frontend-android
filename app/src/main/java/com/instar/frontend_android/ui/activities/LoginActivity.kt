package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityLoginBinding
import com.instar.frontend_android.types.requests.LoginRequest
import com.instar.frontend_android.ui.customviews.ViewEffect
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.FacebookService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var btnNewAccount: ImageButton
    private lateinit var textNewAccount: TextView
    private lateinit var btnLoginAccount: ImageButton
    private lateinit var textLoginAccount: TextView
    private lateinit var btnLoginFacebook: Button
    private lateinit var layoutNewAccount: View
    private lateinit var facebookAvatar: ImageView
    private lateinit var facebookName: TextView
    private var facebookService = FacebookService(this)
    private var authService = ServiceBuilder.buildService(AuthService::class.java, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnNewAccount = binding.newAccount.imageButton
        textNewAccount = binding.newAccount.textNote
        btnLoginAccount = binding.loginAccount.imageButton
        textLoginAccount = binding.loginAccount.textNote
        btnLoginFacebook = binding.btnLoginFacebook
        facebookAvatar = binding.facebookAvatar
        facebookName = binding.facebookName

        loadActivity()
        initView()
    }

    private fun loadActivity() {
        btnLoginFacebook.text = "Tiếp tục bằng Facebook"
        textLoginAccount.text = "Đăng nhập bằng tài khoản khác"
        textNewAccount.text = "Tạo tài khoản mới"
        btnNewAccount.setBackgroundResource(R.drawable.selector_btn_color_login)
        btnLoginAccount.setBackgroundResource(R.drawable.selector_btn_color_black_login)
        textNewAccount.setTextColor(Color.parseColor("#4558FF"))
        textLoginAccount.setTextColor(Color.parseColor("#2F2F2F"))
        facebookService.getUserPublicProfile { jsonObject ->
            val fullName = jsonObject?.optString("name")
            val pictureUrl = jsonObject
                ?.getJSONObject("picture")
                ?.getJSONObject("data")
                ?.optString("url")
            facebookName.text = fullName
            Glide.with(this)
                .load(pictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(facebookAvatar)
        }
    }

    private fun initView() {
        btnLoginFacebook.setOnClickListener {
            facebookService.getUserPublicProfile(fields = "id") { jsonObject ->
                val loginRequest = LoginRequest().apply {
                    val userId = jsonObject?.optString("id")
                    email = "fb_$userId"
                    password = "facebook"
                }

                authService.login(loginRequest).handleResponse(
                    onSuccess = { response ->
                        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("accessToken", response.data?.accessToken)
                            putString("refreshToken", response.data?.refreshToken)
                            apply()
                        }
                        val intent = Intent(this@LoginActivity, MainScreenActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this@LoginActivity, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                    },
                    onError = { error ->
                        Log.e(this::class.java.name, "Error: $error")
                        Toast.makeText(this@LoginActivity, "Đăng nhập không thành công", Toast.LENGTH_LONG).show();
                    }
                )
            }
        }
        btnLoginAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, LoginOtherActivity::class.java)
            startActivity(intent)
        }
        btnNewAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        effectClick()
    }

    private fun effectClick() {
        ViewEffect.ViewButton(btnNewAccount, textNewAccount)
        ViewEffect.ViewButton(btnLoginAccount, textLoginAccount)
    }
}
