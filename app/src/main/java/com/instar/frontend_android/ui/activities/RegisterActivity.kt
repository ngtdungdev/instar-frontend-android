package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityRegisterBinding
import com.instar.frontend_android.databinding.EdittextLoginBinding
import com.instar.frontend_android.types.requests.RegisterRequest
import com.instar.frontend_android.ui.customviews.ViewEditText
import com.instar.frontend_android.ui.customviews.ViewEffect
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.utils.Helpers

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var title: TextView
    private lateinit var textNote: TextView
    private lateinit var emailText: EditText
    private lateinit var labelEmail: TextView
    private lateinit var passwordText: EditText
    private lateinit var labelPassword: TextView
    private lateinit var passwordConfirmText: EditText
    private lateinit var labelPasswordConfirm: TextView
    private lateinit var fullnameText: EditText
    private lateinit var labelFullname: TextView
    private lateinit var usernameText: EditText
    private lateinit var labelUsername: TextView
    private lateinit var btnPasswordEyes: ImageButton
    private lateinit var btnPasswordConfirmEyes: ImageButton
    private lateinit var btnEmail: ImageButton
    private lateinit var btnUsername: ImageButton
    private lateinit var btnFullname: ImageButton
    private lateinit var registerBtn: Button
    private lateinit var emailLayout: EdittextLoginBinding
    private lateinit var passwordLayout: EdittextLoginBinding
    private lateinit var passwordConfirmLayout: EdittextLoginBinding
    private lateinit var fullnameLayout: EdittextLoginBinding
    private lateinit var usernameLayout: EdittextLoginBinding
    private lateinit var layout: View

    private lateinit var imageBack: ImageButton

    private lateinit var authService: AuthService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authService = ServiceBuilder.buildService(AuthService::class.java, this)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textNote = binding.registerTitle.textNote
        title = binding.registerTitle.Title

        emailLayout = binding.email
        emailText = emailLayout.editText
        labelEmail = emailLayout.textView
        btnEmail = emailLayout.btnImage

        passwordLayout = binding.password
        labelPassword = passwordLayout.textView
        passwordText = passwordLayout.editText
        btnPasswordEyes = passwordLayout.btnImage

        passwordConfirmLayout = binding.passwordConfirmation
        labelPasswordConfirm = passwordConfirmLayout.textView
        passwordConfirmText = passwordConfirmLayout.editText
        btnPasswordConfirmEyes = passwordConfirmLayout.btnImage

        fullnameLayout = binding.fullname
        labelFullname = fullnameLayout.textView
        fullnameText = fullnameLayout.editText
        btnFullname = fullnameLayout.btnImage

        usernameLayout = binding.username
        labelUsername = usernameLayout.textView
        usernameText = usernameLayout.editText
        btnUsername = usernameLayout.btnImage

        registerBtn = binding.registerBtn

        layout = binding.registerLayout

        imageBack = binding.imageBack

        loadActivity()
        initView()
    }


    private fun loadActivity() {
        title.text = "Tạo tài khoản"
        textNote.text = "Nhập tên người dùng, email"

        labelEmail.text = "Tên người dùng, email"
        emailText.hint = "Tên người dùng, email"

        labelFullname.text = "Họ và tên"
        fullnameText.hint = "Họ và tên"

        labelUsername.text = "Tên người dùng"
        usernameText.hint = "Tên người dùng"

        labelPassword.text = "Mật khẩu"
        passwordText.hint = "Mật khẩu"
        passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        labelPasswordConfirm.text = "Nhập lại mật khẩu"
        passwordConfirmText.hint = "Nhập lại mật khẩu"
        passwordConfirmText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        btnPasswordConfirmEyes.setBackgroundResource(R.drawable.ic_instagram_eyes_off)
        btnPasswordEyes.setBackgroundResource(R.drawable.ic_instagram_eyes_off)
        registerBtn.text = "Đăng ký"
    }

    private lateinit var listEditText:Array<ViewEditText>
    private fun initView() {
        listEditText = Array(10) { ViewEditText() }
        for (i in 0..4) {
            listEditText[i].setOnItemFocusClick(object : ViewEditText.OnItemClick {
                override fun onEyesChange(view: View) {
                    checkFocus(i)
                }
                override fun onRemoveChange(view: View) {
                    checkFocus(i)
                }
            })
            when(i) {
                0 -> listEditText[i].EditTextRemove(fullnameLayout.Layout, fullnameText, labelFullname, btnFullname)
                1 -> listEditText[i].EditTextRemove(usernameLayout.Layout, usernameText, labelUsername, btnUsername)
                2 -> listEditText[i].EditTextRemove(emailLayout.Layout, emailText, labelEmail, btnEmail)
                3 -> listEditText[i].EditTextEyes(passwordLayout.Layout, passwordText, labelPassword, btnPasswordEyes)
                4 -> listEditText[i].EditTextEyes(passwordConfirmLayout.Layout, passwordConfirmText, labelPasswordConfirm, btnPasswordConfirmEyes)
            }
        }

        ViewEffect.ViewText(registerBtn)
        registerBtn.setOnClickListener {
            if (!Helpers.isValidEmail(emailText.text.toString())
                || emailText.text.toString().isEmpty()
                || usernameText.text.toString().isEmpty()
                || fullnameText.text.toString().isEmpty()
                || passwordText.text.toString().isEmpty()) {
                return@setOnClickListener;
            }

            if (!passwordText.text.toString().equals(passwordConfirmText.text.toString())) {
                return@setOnClickListener;
            }

            val registerRequest = RegisterRequest().apply {
                email = emailText.text.toString()
                username = usernameText.text.toString()
                password = passwordText.text.toString()
                fullname = fullnameText.text.toString()
            }

            authService.register(registerRequest).handleResponse(
                onSuccess = { response ->
                    // Handle successful response
                    val intent = Intent(this@RegisterActivity, LoginOtherActivity::class.java)
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

            passwordText.clearFocus()
            if (passwordText.text.toString().isEmpty()) {
                setPassword()
            }

            passwordConfirmText.clearFocus()
            if (passwordConfirmText.text.toString().isEmpty()) {
                setPasswordConfirm()
            }

            usernameText.clearFocus()
            if (usernameText.text.toString().isEmpty()) {
                setUserName()
            }

            fullnameText.clearFocus()
            if (fullnameText.text.toString().isEmpty()) {
                setFullName()
            }
        }

        imageBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkFocus(index: Int) {
        for (i in 0..4) {
            if(listEditText[i].returnFocus() && i != index) {
                when (i) {
                    0 -> setFullName()
                    1 -> setUserName()
                    2 -> setEmail()
                    3 -> setPassword()
                    4 -> setPasswordConfirm()
                }
            }
        }

    }
    private fun setEmail() {
        labelEmail.visibility = View.GONE
        emailLayout.Layout.setBackgroundResource(R.drawable.border_component_login_dow)
        emailText.hint = "Tên người dùng, email"
    }

    private fun setPassword() {
        labelPassword.visibility = View.GONE
        passwordLayout.Layout.setBackgroundResource(R.drawable.border_component_login_dow)
        passwordText.hint = "Mật khẩu"
    }
    private fun setPasswordConfirm() {
        labelPasswordConfirm.visibility = View.GONE
        passwordConfirmLayout.Layout.setBackgroundResource(R.drawable.border_component_login_dow)
        passwordConfirmText.hint = "Nhập lại mật khẩu"
    }

    private fun setFullName() {
        labelFullname.visibility = View.GONE
        fullnameLayout.Layout.setBackgroundResource(R.drawable.border_component_login_dow)
        fullnameText.hint = "Họ và tên"
    }

    private fun setUserName() {
        labelUsername.visibility = View.GONE
        usernameLayout.Layout.setBackgroundResource(R.drawable.border_component_login_dow)
        usernameText.hint = "Tên người dùng"
    }
}