package com.instar.frontend_android.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.instar.frontend_android.R;
import com.instar.frontend_android.ui.customviews.ViewEffect;

public class LoginActivity extends AppCompatActivity {
    private ImageButton btnNewAccount;
    private TextView textNewAccount;
    private ImageButton btnLoginAccount;
    private TextView textLoginAccount;
    private Button btnLoginFacebook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
        btnNewAccount = findViewById(R.id.newAccount).findViewById(R.id.imageButton);
        textNewAccount = findViewById(R.id.newAccount).findViewById(R.id.textNote);
        btnLoginAccount = findViewById(R.id.loginAccount).findViewById(R.id.imageButton);
        textLoginAccount = findViewById(R.id.loginAccount).findViewById(R.id.textNote);
        loadActivity();
        initView();
    }

    public void loadActivity() {
        textNewAccount.setText("Tạo tài khoản mới");
        textLoginAccount.setText("Đăng nhập bằng tài khoản khác");
        btnLoginFacebook.setText("Tiếp tục bằng Facebook");
        btnNewAccount.setBackgroundResource(R.drawable.selector_btn_color_login);
        btnLoginAccount.setBackgroundResource(R.drawable.selector_btn_color_black_login);
        textNewAccount.setTextColor(Color.parseColor("#4558FF"));
        textLoginAccount.setTextColor(Color.parseColor("#2F2F2F"));
    }

    public void initView() {
        btnLoginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginOtherActivity.class);
                startActivity(intent);
            }
        });
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        effectClick();
    }

    public void effectClick() {
        ViewEffect.ViewButton(btnNewAccount,textNewAccount);
        ViewEffect.ViewButton(btnLoginAccount,textLoginAccount);
    }
}
