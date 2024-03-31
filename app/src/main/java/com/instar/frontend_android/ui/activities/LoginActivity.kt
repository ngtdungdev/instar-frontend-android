package com.instar.frontend_android.ui.activities
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityLoginBinding
import com.instar.frontend_android.ui.customviews.ViewEffect


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var btnNewAccount: ImageButton
    private lateinit var textNewAccount: TextView
    private lateinit var btnLoginAccount: ImageButton
    private lateinit var textLoginAccount: TextView
    private lateinit var btnLoginFacebook: Button
    private lateinit var layoutNewAccount: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnNewAccount = binding.newAccount.imageButton
        textNewAccount = binding.newAccount.textNote
        btnLoginAccount = binding.loginAccount.imageButton
        textLoginAccount = binding.loginAccount.textNote
        btnLoginFacebook = binding.btnLoginFacebook

        loadActivity()
         initView()
    }

    private fun loadActivity() {
        textNewAccount.text = "Tạo tài khoản mới"
        textLoginAccount.text = "Đăng nhập bằng tài khoản khác"
        btnLoginFacebook.text = "Tiếp tục bằng Facebook"
        btnNewAccount.setBackgroundResource(R.drawable.selector_btn_color_login)
        btnLoginAccount.setBackgroundResource(R.drawable.selector_btn_color_black_login)
        textNewAccount.setTextColor(Color.parseColor("#4558FF"))
        textLoginAccount.setTextColor(Color.parseColor("#2F2F2F"))
    }

    private fun initView() {
        btnLoginAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, LoginOtherActivity::class.java)
            startActivity(intent)
        }
        btnLoginFacebook.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainScreenActivity::class.java)
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
