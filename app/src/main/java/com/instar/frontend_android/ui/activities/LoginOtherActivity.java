package com.instar.frontend_android.ui.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.instar.frontend_android.R;
import com.instar.frontend_android.ui.customviews.ViewEffect;


public class LoginOtherActivity extends AppCompatActivity {
    private EditText emailText;
    private TextView labelEmail;
    private EditText passwordText;
    private TextView labelPassword;
    private ImageButton btnRemove;
    private TextView btnNewPassWord;
    private TextView textNewAccount;
    private ImageButton btnNewAccount;
    private Button btnLogin;
    private View layoutLogin;
    private View emailLayout;
    private View passWordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_other);
        emailLayout = findViewById(R.id.email);
        passWordLayout = findViewById(R.id.passWord);
        emailText = emailLayout.findViewById(R.id.editText);
        labelEmail = emailLayout.findViewById(R.id.textView);
        labelPassword = passWordLayout.findViewById(R.id.textView);
        passwordText = passWordLayout.findViewById(R.id.editText);
        layoutLogin = findViewById(R.id.LayoutBtnLogin);
        btnRemove = findViewById(R.id.btnRemove);
        btnNewPassWord = findViewById(R.id.btnNewPassWord);
        btnNewAccount = findViewById(R.id.loginAccount).findViewById(R.id.imageButton);
        textNewAccount = findViewById(R.id.loginAccount).findViewById(R.id.textView);
        btnLogin = findViewById(R.id.btnLoginFacebook);
        loadActivity();
        initView();
    }

    public void loadActivity() {
        labelEmail.setText("Tên người dùng, email/số di động");
        labelPassword.setText("Mật khẩu");
        emailText.setHint("Tên người dùng, email/số di động");
        passwordText.setHint("Mật khẩu");
        btnLogin.setText("Đăng nhập");
        btnNewPassWord.setText("Bạn quên mật khẩu ư?");
        textNewAccount.setText("Tạo tài khoản mới");
    }
    public void initView() {
        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                labelEmail.setVisibility(View.VISIBLE);
                emailText.setHint("");
                emailLayout.setBackground(getDrawable(R.drawable.border_component_login_up));
                if (!emailText.getText().toString().isEmpty()) btnRemove.setVisibility(View.VISIBLE);
                if (passwordText.getText().toString().isEmpty()) setPassword();
            }
        });
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (emailText.getText().toString().isEmpty()) {
                    btnRemove.setVisibility(View.GONE);
                } else btnRemove.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailText.setText("");
                btnRemove.setVisibility(View.GONE);
            }
        });
        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                labelPassword.setVisibility(View.VISIBLE);
                passwordText.setHint("");
                passWordLayout.setBackground(getDrawable(R.drawable.border_component_login_up));
                if (emailText.getText().toString().isEmpty()) setEmail();
            }
        });
        layoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                emailText.clearFocus();
                passwordText.clearFocus();
                if (emailText.getText().toString().isEmpty()) {
                    setEmail();
                }
                if (passwordText.getText().toString().isEmpty()) {
                    setPassword();
                }
            }
        });

        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOtherActivity.this, LoginEmailActivity.class);
                startActivity(intent);
            }
        });
        effectClick();
    }

    public void effectClick() {
        ViewEffect.ViewText(btnNewPassWord);
        ViewEffect.ViewButton(btnNewAccount, textNewAccount);
    }
    public void setEmail() {
        labelEmail.setVisibility(View.GONE);
        emailLayout.setBackground(getDrawable(R.drawable.border_component_login_dow));
        emailText.setHint("Tên người dùng, email/số di động");
    }

    public void setPassword() {
        labelPassword.setVisibility(View.GONE);
        passWordLayout.setBackground(getDrawable(R.drawable.border_component_login_dow));
        passwordText.setHint("Mật khẩu");
    }
}
