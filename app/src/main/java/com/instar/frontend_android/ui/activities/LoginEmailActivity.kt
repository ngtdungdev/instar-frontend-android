package com.instar.frontend_android.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityLoginEmailBinding
import com.instar.frontend_android.databinding.EdittextLoginBinding
import com.instar.frontend_android.ui.customviews.ViewEditText
import com.instar.frontend_android.ui.customviews.ViewEffect

class LoginEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginEmailBinding

    private lateinit var imageBack: ImageButton
    private lateinit var btnText: TextView
    private lateinit var title: TextView
    private lateinit var textNote: TextView
    private lateinit var layout: View
    private lateinit var layoutEmail: EdittextLoginBinding
    private lateinit var emailText: EditText
    private lateinit var labelEmail: TextView
    private lateinit var btnRemove: ImageButton
    private lateinit var btnFindAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        imageBack = binding.imageBack
        layout = binding.layout
        layoutEmail = binding.email
        emailText = layoutEmail.editText
        labelEmail = layoutEmail.textView
        btnRemove = layoutEmail.btnImage
        title = binding.emailTitle.Title
        textNote = binding.emailTitle.textNote
        btnText = binding.emailTitle.btnText
        btnFindAccount = binding.btnFindAccount

        loadActivity()
        initView()
    }

    private fun loadActivity() {
        title.text = "Tìm tài khoản"
        textNote.text = "Nhập tên người dùng, email hoặc số di động của bạn"
        btnText.text = "Bạn không thể đặt lại mật khẩu?"
        labelEmail.text = "Tên người dùng, email/số di động"
        emailText.hint = "Tên người dùng, email/số di động"
        btnFindAccount.text = "Tìm tài khoản"
    }

    private fun initView() {
        val viewEditText = ViewEditText()
        viewEditText.EditTextRemove(layoutEmail.root, emailText, labelEmail, btnRemove)
        ViewEffect.ViewText(btnText)
        btnText.setOnClickListener {
//            val authService = ServiceBuilder.buildService(AuthService::class.java)
//
//            val loginRequest = LoginRequest()
//            loginRequest.email = "example@email.com"
//            loginRequest.password = "password123"
//
//            authService.login(loginRequest).enqueue(object : Callback<ApiResponse<AuthResponse>> {
//                override fun onResponse(
//                    call: Call<ApiResponse<AuthResponse>>,
//                    response: Response<ApiResponse<AuthResponse>>
//                ) {
//                    if (response.isSuccessful) {
//                        val authResponse = response.body()
//
//                        Log.e("authResponse", authResponse!!.data.accessToken + "")
//                        // Xử lý authResponse ở đây
//                    } else {
//                        // Xử lý lỗi nếu cần
//                    }
//                }
//
//                override fun onFailure(call: Call<ApiResponse<AuthResponse>>, t: Throwable) {
//                    Log.e("authResponse", "1123")
//                }
//            })
        }

        btnFindAccount.setOnClickListener {
            val intent = Intent(this@LoginEmailActivity, LoginSmsActivity::class.java)
            startActivity(intent)
        }

        layout.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            emailText.clearFocus()
            if (emailText.text.toString().isEmpty()) {
                setEmail()
            }
        }

        imageBack.setOnClickListener { finish() }

        effectClick()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setEmail() {
        labelEmail.visibility = View.GONE
        layoutEmail.root.background = getDrawable(R.drawable.border_component_login_dow)
        emailText.hint = "Tên người dùng, email/số di động"
    }

    private fun effectClick() {
        ViewEffect.ImageBack(imageBack)
    }
}