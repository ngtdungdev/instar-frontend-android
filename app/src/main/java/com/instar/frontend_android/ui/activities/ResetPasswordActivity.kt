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
import com.instar.frontend_android.databinding.ActivityLoginPasswordBinding
import com.instar.frontend_android.databinding.EdittextLoginBinding
import com.instar.frontend_android.types.requests.ResetPasswordRequest
import com.instar.frontend_android.ui.customviews.ViewEditText
import com.instar.frontend_android.ui.customviews.ViewEffect
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginPasswordBinding
    private lateinit var imageBack: ImageButton
    private lateinit var btnText: TextView
    private lateinit var title: TextView
    private lateinit var textNote: TextView
    private lateinit var layout: View

    private lateinit var layoutPassword: EdittextLoginBinding
    private lateinit var layoutRepeatPassword: EdittextLoginBinding

    private lateinit var passwordText: EditText
    private lateinit var labelPassword: TextView
    private lateinit var btnEyesPassword: ImageButton

    private lateinit var repeatPasswordText: EditText
    private lateinit var labelRepeatPassword: TextView
    private lateinit var btnEyesRepeatPassword: ImageButton

    private lateinit var layoutSms: EdittextLoginBinding
    private lateinit var SmsText: EditText
    private lateinit var labelSms: TextView
    private lateinit var btnEyes: ImageButton

    private lateinit var btnConfirm: Button

    private lateinit var authService: AuthService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authService = ServiceBuilder.buildService(AuthService::class.java, this)

        binding = ActivityLoginPasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)
        imageBack = binding.imageBack
        layout = binding.passwordLayout
        layoutPassword = binding.passWord
        layoutRepeatPassword = binding.passWordConfirmation

        passwordText = layoutPassword.editText
        labelPassword = layoutPassword.textView
        btnEyesPassword = layoutPassword.btnImage

        repeatPasswordText = layoutRepeatPassword.editText
        labelRepeatPassword = layoutRepeatPassword.textView
        btnEyesRepeatPassword= layoutRepeatPassword.btnImage

        title = binding.passWordTitle.Title
        textNote = binding.passWordTitle.textNote
        btnText = binding.passWordTitle.btnText

        layoutSms = binding.sms
        SmsText = layoutSms.editText
        labelSms = layoutSms.textView
        btnEyes = layoutSms.btnImage

        btnText = binding.passWordTitle.btnText

        btnConfirm = binding.btnPassWord
        loadActivity()
        initView()
    }
    private fun loadActivity() {
        title.text = "Thay đổi mật khẩu"
        textNote.text = "Không tiết lộ mật khẩu cho người khác để tránh trường hợp mất tài khoản"

        passwordText.hint = "Mật khẩu mới"
        labelPassword.text = "Mật khẩu mới"
        repeatPasswordText.hint = "Nhập lại mật khẩu mới "
        labelRepeatPassword.text = "Nhập lại mật khẩu mới"
        labelSms.text = "Nhập mã"
        SmsText.hint = "Nhập mã"
        btnConfirm.text = "Xác nhận"
        passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        repeatPasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        SmsText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        btnEyes.setBackgroundResource(R.drawable.ic_instagram_eyes_off)
        btnEyesPassword.setBackgroundResource(R.drawable.ic_instagram_eyes_off)
        btnEyesRepeatPassword.setBackgroundResource(R.drawable.ic_instagram_eyes_off)
    }

    private fun initView() {
        val emailVerifyCode = intent.getStringExtra("email")
        val viewEditText = ViewEditText()
        val viewEditText1 = ViewEditText()
        val viewEditText2 = ViewEditText()
        viewEditText1.EditTextEyes(layoutPassword.Layout, passwordText, labelPassword, btnEyesPassword)
        viewEditText2.EditTextEyes(layoutRepeatPassword.Layout, repeatPasswordText, labelRepeatPassword, btnEyesRepeatPassword)
        viewEditText.EditTextEyes(layoutSms.Layout, SmsText, labelSms, btnEyes)
        ViewEffect.ViewText(btnText)
        viewEditText1.setOnItemFocusClick(object : ViewEditText.OnItemClick {
            override fun onEyesChange(view: View) {
                if (repeatPasswordText.text.toString().isEmpty()) setRepeatPassword()
                if (SmsText.text.toString().isEmpty()) setSms()
            }
            override fun onRemoveChange(view: View) {
            }
        })
        viewEditText2.setOnItemFocusClick(object : ViewEditText.OnItemClick {
            override fun onEyesChange(view: View) {
                if (passwordText.text.toString().isEmpty()) setPassword()
                if (SmsText.text.toString().isEmpty()) setSms()
            }
            override fun onRemoveChange(view: View) {
            }
        })
        viewEditText.setOnItemFocusClick(object : ViewEditText.OnItemClick {
            override fun onEyesChange(view: View) {
                if (passwordText.text.toString().isEmpty()) setPassword()
                if (repeatPasswordText.text.toString().isEmpty()) setRepeatPassword()
            }
            override fun onRemoveChange(view: View) {
            }
        })
        ViewEffect.ViewText(btnText)
        btnText.setOnClickListener {  }

        btnConfirm.setOnClickListener {
            if (SmsText.text.toString().isEmpty() || repeatPasswordText.text.toString().isEmpty() || passwordText.text.toString().isEmpty()) {
                return@setOnClickListener;
            }

            if (!repeatPasswordText.text.toString().equals(passwordText.text.toString())) {
                return@setOnClickListener;
            }

            val resetPasswordRequest = ResetPasswordRequest().apply {
                verifyCode = SmsText.text.toString()
                newPassword = passwordText.text.toString()
                email = emailVerifyCode
            }

            authService.resetPassword(resetPasswordRequest).handleResponse(
                onSuccess = { authResponse ->
                    val intent = Intent(this@ResetPasswordActivity, LoginOtherActivity::class.java)
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
            passwordText.clearFocus()
            repeatPasswordText.clearFocus()
            SmsText.clearFocus()
            passwordText.clearFocus()
            if (passwordText.text.toString().isEmpty()) {
                setPassword()
            }
            if (repeatPasswordText.text.toString().isEmpty()) {
                setRepeatPassword()
            }
            if (SmsText.text.toString().isEmpty()) {
                setSms()
            }
        }

        imageBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun setSms() {
        labelSms.visibility = View.GONE
        layoutSms.Layout.background = getDrawable(R.drawable.border_component_login_dow)
        SmsText.hint = "Nhập mã"
    }

    private fun setPassword() {
        labelPassword.visibility = View.GONE
        layoutPassword.Layout.background = getDrawable(R.drawable.border_component_login_dow)
        passwordText.hint = "Mật khẩu mới"
    }
    private fun setRepeatPassword() {
        labelRepeatPassword.visibility = View.GONE
        layoutRepeatPassword.Layout.background = getDrawable(R.drawable.border_component_login_dow)
        repeatPasswordText.hint = "Nhập lại mật khẩu mới"
    }
    private fun effectClick() {
        ViewEffect.ViewText(btnConfirm)
    }
}