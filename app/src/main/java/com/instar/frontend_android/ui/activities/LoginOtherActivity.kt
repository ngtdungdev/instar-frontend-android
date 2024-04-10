package com.instar.frontend_android.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.gson.Gson
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityLoginOtherBinding
import com.instar.frontend_android.databinding.EdittextLoginBinding
import com.instar.frontend_android.types.requests.LoginRequest
import com.instar.frontend_android.ui.customviews.ViewEditText
import com.instar.frontend_android.ui.customviews.ViewEffect
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.FacebookService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.utils.Helpers


class LoginOtherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginOtherBinding

    private lateinit var emailText: EditText
    private lateinit var labelEmail: TextView
    private lateinit var passwordText: EditText
    private lateinit var labelPassword: TextView
    private lateinit var btnRemove: ImageButton
    private lateinit var btnEyes: ImageButton
    private lateinit var btnNewPassWord: TextView
    private lateinit var textNewAccount: TextView
    private lateinit var btnNewAccount: ImageButton
    private lateinit var btnLogin: Button
    private lateinit var layoutLogin: View
    private lateinit var emailLayout: EdittextLoginBinding
    private lateinit var passwordLayout: EdittextLoginBinding
    private lateinit var btnLoginWithFacebook: ImageButton
    private lateinit var textLoginWithFacebook: TextView
    private lateinit var authService: AuthService
    private lateinit var facebookService: FacebookService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authService = ServiceBuilder.buildService(AuthService::class.java, this)
        facebookService = FacebookService(this@LoginOtherActivity)
        binding = ActivityLoginOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailLayout = binding.email
        passwordLayout = binding.passWord
        emailText = emailLayout.editText
        labelEmail = emailLayout.textView
        labelPassword = passwordLayout.textView
        passwordText = passwordLayout.editText
        layoutLogin = binding.LayoutBtnLogin
        btnRemove = emailLayout.btnImage
        btnEyes = passwordLayout.btnImage
        btnNewPassWord = binding.btnNewPassWord
        btnNewAccount = binding.loginAccount.imageButton
        textNewAccount = binding.loginAccount.textNote

        btnLoginWithFacebook = binding.btnLoginWithFacebook.imageButton
        textLoginWithFacebook = binding.btnLoginWithFacebook.textNote

        btnLogin = binding.btnLoginFacebook

        loadActivity()
        initView()
    }

    private fun loadActivity() {
        labelEmail.text = "Tên người dùng, email/số di động"
        labelPassword.text = "Mật khẩu"
        emailText.hint = "Tên người dùng, email/số di động"
        passwordText.hint = "Mật khẩu"
        btnLogin.text = "Đăng nhập"
        btnNewPassWord.text = "Bạn quên mật khẩu ư?"
        textNewAccount.text = "Tạo tài khoản mới"
        textLoginWithFacebook.text = "Đăng nhập bằng Facebook"
        btnNewAccount.setBackgroundResource(R.drawable.selector_btn_color_login)
        btnLoginWithFacebook.setBackgroundResource(R.drawable.selector_btn_color_login)
        textNewAccount.setTextColor(Color.parseColor("#4558FF"))
        textLoginWithFacebook.setTextColor(Color.parseColor("#4558FF"))
        passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        btnEyes.setBackgroundResource(R.drawable.ic_instagram_eyes_off)
    }

    private fun initView() {
        val viewEditText = ViewEditText()

        viewEditText.EditTextRemove(emailLayout.Layout, emailText, labelEmail, btnRemove)
        viewEditText.EditTextEyes(passwordLayout.Layout, passwordText, labelPassword, btnEyes)
        viewEditText.setOnItemFocusClick(object : ViewEditText.OnItemClick {
            override fun onEyesChange(view: View) {
                if (emailText.text.toString().isEmpty()) setEmail()
            }

            override fun onRemoveChange(view: View) {
                if (passwordText.text.toString().isEmpty()) setPassword()
            }
        })
        layoutLogin.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            emailText.clearFocus()
            passwordText.clearFocus()
            if (emailText.text.toString().isEmpty()) {
                setEmail()
            }
            if (passwordText.text.toString().isEmpty()) {
                setPassword()
            }
        }

        btnNewAccount.setOnClickListener {
            val intent = Intent(this@LoginOtherActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        facebookService.addListeners(
            onSuccess = { token ->
                println("Successfully logged into Facebook")
                println(Gson().toJson(token))
                println(token.token)
            },
            onCancel = {
                println("Cancelled logging into Facebook")
            },
            onError = { error ->
                println("Error while logging into Facebook")
                println(error)
            })

        btnLoginWithFacebook.setOnClickListener {
            println("Facebook login clicked")
            facebookService.login()
        }

        btnLogin.setOnClickListener {
            if (!Helpers.isValidEmail(emailText.text.toString()) || emailText.text.toString().isEmpty() || passwordText.text.toString().isEmpty()) {
                return@setOnClickListener;
            }

            val loginRequest = LoginRequest().apply {
                email = emailText.text.toString();
                password = passwordText.text.toString()
            }

            authService.login(loginRequest).handleResponse(
                onSuccess = { authResponse ->
                    // Khởi tạo SharedPreferences
                    val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

                    val accessToken = authResponse.data?.accessToken
                    val refreshToken = authResponse.data?.refreshToken

                    with(sharedPreferences.edit()) {
                        putString("accessToken", accessToken)
                        apply()
                    }

                    with(sharedPreferences.edit()) {
                        putString("refreshToken", refreshToken)
                        apply()
                    }

                    val intent = Intent(this@LoginOtherActivity, MainScreenActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(this@LoginOtherActivity, "Login successful", Toast.LENGTH_LONG).show();
                },
                onError = { error ->
                    // Handle error
                    Log.e("ServiceBuilder", "Error: $error")
                    Toast.makeText(this@LoginOtherActivity, "Login failure: Email hoặc mật khẩu không đúng", Toast.LENGTH_LONG).show();
                }
            )

        }

        btnNewPassWord.setOnClickListener {
            val intent = Intent(this@LoginOtherActivity, LoginEmailActivity::class.java)
            startActivity(intent)
        }

        effectClick()
    }


    private fun effectClick() {
        ViewEffect.ViewText(btnNewPassWord)
        ViewEffect.ViewButton(btnNewAccount, textNewAccount)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setEmail() {
        labelEmail.visibility = View.GONE
        emailLayout.Layout.background = getDrawable(R.drawable.border_component_login_dow)
        emailText.hint = "Tên người dùng, email/số di động"
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setPassword() {
        labelPassword.visibility = View.GONE
        passwordLayout.Layout.background = getDrawable(R.drawable.border_component_login_dow)
        passwordText.hint = "Mật khẩu"
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookService.onActivityResult(requestCode, resultCode, data)
    }
}