package com.instar.frontend_android.ui.activities

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.EdittextLoginBinding
import com.instar.frontend_android.databinding.ActivityLoginSmsBinding
import com.instar.frontend_android.ui.customviews.ViewEditText
import com.instar.frontend_android.ui.customviews.ViewEffect


class LoginSmsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginSmsBinding

    private lateinit var imageBack: ImageButton
    private lateinit var btnText: TextView
    private lateinit var title: TextView
    private lateinit var textNote: TextView
    private lateinit var layout: View
    private lateinit var layoutSms: EdittextLoginBinding
    private lateinit var SmsText: EditText
    private lateinit var labelSms: TextView
    private lateinit var btnEyes: ImageButton
    private lateinit var btnNewSms: TextView
    private lateinit var btnSms: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginSmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageBack = binding.imageBack
        layout = binding.layout
        layoutSms = binding.sms
        SmsText = layoutSms.editText
        labelSms = layoutSms.textView
        btnEyes = layoutSms.btnImage
        title = binding.smsTitle.Title
        textNote = binding.smsTitle.textNote
        btnText = binding.smsTitle.btnText
        btnNewSms = binding.btnNewSms
        btnSms = binding.btnSms

        loadActivity()
        initView()
    }

    private fun loadActivity() {
        title.text = "Xác nhận tài khoản"
        textNote.text = "Chúng tôi đã gửi mã đến email của bạn. Hãy nhập mã đó để xác nhận tài khoản"
        btnText.text = "Không thể xác nhận tài khoản?"
        labelSms.text = "Nhập mã"
        btnNewSms.text = "Gửi lại mã"
        SmsText.hint = "Nhập mã"
        btnSms.text = "Tiếp tục"
        SmsText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        btnEyes.setBackgroundResource(R.drawable.ic_instagram_eyes_off)
    }

    private fun initView() {
        val viewEditText = ViewEditText()
        viewEditText.EditTextEyes(layoutSms.root, SmsText, labelSms, btnEyes)
        ViewEffect.ViewText(btnText)
        btnText.setOnClickListener { }

        layout.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            SmsText.clearFocus()
            if (SmsText.text.toString().isEmpty()) {
                setSms()
            }
        }

        imageBack.setOnClickListener { finish() }

        effectClick()
    }

    private fun setSms() {
        labelSms.visibility = View.GONE
        layoutSms.root.background = getDrawable(R.drawable.border_component_login_dow)
        SmsText.hint = "Nhập mã"
    }

    private fun effectClick() {
        ViewEffect.ViewText(btnNewSms)
        ViewEffect.ImageBack(imageBack)
    }
}