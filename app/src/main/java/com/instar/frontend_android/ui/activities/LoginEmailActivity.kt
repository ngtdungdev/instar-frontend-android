package com.instar.frontend_android.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityLoginEmailBinding
import com.instar.frontend_android.databinding.EdittextLoginBinding
import com.instar.frontend_android.types.requests.LoginRequest
import com.instar.frontend_android.types.requests.VerifyCodeRequest
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.ui.customviews.ViewEditText
import com.instar.frontend_android.ui.customviews.ViewEffect
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.utils.Helpers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    private lateinit var authService: AuthService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authService = ServiceBuilder.buildService(AuthService::class.java, this)

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
        viewEditText.EditTextRemove(layoutEmail.Layout, emailText, labelEmail, btnRemove)
        ViewEffect.ViewText(btnText)
        btnText.setOnClickListener {
        }

        btnFindAccount.setOnClickListener {
            if (!Helpers.isValidEmail(emailText.text.toString()) || emailText.text.toString().isEmpty()) {
                return@setOnClickListener;
            }

            val verifyCodeRequest = VerifyCodeRequest().apply {
                email = emailText.text.toString()
            }

            authService.verifyCode(verifyCodeRequest).handleResponse(
                onSuccess = { response ->
                    // Handle successful response
                    val intent = Intent(this@LoginEmailActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("email", verifyCodeRequest.email)
                    startActivity(intent)
                },
                onError = { error ->
                    // Handle error
                    Log.e("ServiceBuilder", "Error: $error")
                }
            )

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
        layoutEmail.Layout.background = getDrawable(R.drawable.border_component_login_dow)
        emailText.hint = "Tên người dùng, email/số di động"
    }

    private fun effectClick() {
        ViewEffect.ImageBack(imageBack)
    }
}